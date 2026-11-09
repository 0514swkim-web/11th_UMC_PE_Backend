package com.umc.movie.dto;

// 로그인 성공 시 result에 담기는 값
public record LoginResponse(
        String nickname,
        String accessToken
) {
}
