package com.umc.movie.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// PATCH /members/me 요청 바디 (이번 주차는 닉네임만 수정)
public record MemberUpdateRequest(

        @Schema(description = "바꿀 닉네임 (2자 이상 12자 이하)", example = "영화광")
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하여야 합니다.")
        String nickname
) {
}
