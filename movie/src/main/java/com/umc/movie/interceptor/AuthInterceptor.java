package com.umc.movie.interceptor;

import com.umc.movie.exception.ApiException;
import com.umc.movie.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// 컨트롤러 앞에서 토큰을 검사하는 관문 (건물 로비의 출입 게이트)
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    public static final String MEMBER_ID = "memberId"; // 컨트롤러에서 꺼낼 때 쓰는 이름
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    // 컨트롤러 실행 "전"에 호출됨. true → 컨트롤러로 통과, 예외 → 여기서 끝
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 브라우저의 CORS 사전 요청(preflight)에는 토큰이 안 붙음 → 막으면 프론트 요청이 전부 실패하니 통과
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        // "Bearer {토큰}" 형식이 아니면 토큰을 안 보낸 것으로 봄
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "AUTH401_2",
                    "로그인이 필요한 요청입니다.");
        }

        // 검증 실패면 JwtUtil이 예외를 던짐 → 여기를 지나면 유효한 토큰
        Long memberId = jwtUtil.getMemberId(header.substring(BEARER_PREFIX.length()));

        // 뒤에 실행될 컨트롤러가 꺼내 쓸 수 있게 요청에 담아 둠
        request.setAttribute(MEMBER_ID, memberId);
        return true;
    }
}
