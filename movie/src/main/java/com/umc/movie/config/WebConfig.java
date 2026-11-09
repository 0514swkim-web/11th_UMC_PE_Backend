package com.umc.movie.config;

import com.umc.movie.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor; // 7주차: 토큰 검사 관문

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해
                .allowedOrigins("http://localhost:5173") // 프론트 개발 서버 출처만 허용 ("*" 대신 좁게)
                .allowedMethods("GET", "POST", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    // 7주차: 일단 전부 잠그고, 로그인 없이 써야 하는 경로만 열어 줌
    // (새 API를 만들고 깜빡해도 "잠긴 채"로 남는 안전한 방식)
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/**",             // 로그인
                        "/members",             // 회원가입 (POST /members)
                        "/members/nickname/**", // 닉네임 중복 체크
                        "/members/email/**",    // 이메일 중복 체크
                        "/members/*/ratings",   // 유저별 평점 조회 (누구나 볼 수 있는 목록)
                        "/error"                // 스프링 내부 오류 경로
                );
    }
}
