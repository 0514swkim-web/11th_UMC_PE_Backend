package com.umc.movie.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// POST /auth/login 요청 바디
// 9주차: @Schema의 example 값이 Swagger 입력창에 미리 채워짐
public record LoginRequest(

        @Schema(description = "가입할 때 사용한 이메일", example = "challenger@umc.com")
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        // 로그인 때는 비밀번호 규칙까지 검사하지 않음
        // (규칙이 바뀌기 전에 가입한 회원이 로그인조차 못 하게 되니까)
        @Schema(description = "가입할 때 사용한 비밀번호", example = "Umc1234!")
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password
) {
}
