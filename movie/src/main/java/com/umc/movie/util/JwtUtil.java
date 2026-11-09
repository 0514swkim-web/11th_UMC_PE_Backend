package com.umc.movie.util;

import com.umc.movie.exception.ApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

// JWT를 만들고(로그인 때) 검사하는(요청마다) 도구 클래스
@Component
public class JwtUtil {

    private final SecretKey secretKey;         // 서명용 도장
    private final Duration accessExpiration;   // 토큰 유효시간

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration}") Long accessExpiration
    ) {
        // 문자열 비밀키 → HMAC-SHA 서명용 키 객체. 같은 키로만 검증할 수 있음
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = Duration.ofSeconds(accessExpiration);
    }

    // 로그인에 성공한 회원에게 줄 Access Token 만들기
    public String createAccessToken(Long memberId, String email) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(String.valueOf(memberId)) // 토큰 주인 = memberId
                .claim("email", email)             // 참고용 부가 정보
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(accessExpiration))) // 만료 시각 (없으면 영원히 유효)
                .signWith(secretKey)               // 서명 → 내용을 조작하면 검증에서 걸림
                .compact();
    }

    // 토큰을 검증하고 memberId 꺼내기. 값을 돌려줬다면 "검증 통과한 토큰"
    public Long getMemberId(String token) {
        try {
            return Long.valueOf(getClaims(token).getPayload().getSubject());
        } catch (ExpiredJwtException e) {
            // 만료: 다시 로그인하면 해결되니까 따로 구분
            throw new ApiException(HttpStatus.UNAUTHORIZED, "AUTH401_4",
                    "토큰이 만료되었습니다. 다시 로그인해주세요.");
        } catch (JwtException | IllegalArgumentException e) {
            // 서명 불일치, 형식 오류 등 나머지 전부
            throw new ApiException(HttpStatus.UNAUTHORIZED, "AUTH401_3",
                    "유효하지 않은 토큰입니다.");
        }
    }

    private Jws<Claims> getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey) // 이 키로 서명을 다시 계산해서 대조
                .clockSkewSeconds(60)  // 서버·클라이언트 시계 차이 60초까지 봐줌
                .build()
                .parseSignedClaims(token);
    }
}
