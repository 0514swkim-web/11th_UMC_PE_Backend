package com.umc.movie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// PATCH /members/me 요청 바디 (이번 주차는 닉네임만 수정)
public record MemberUpdateRequest(

        @NotBlank(message = "닉네임을 입력해주세요.")
        @Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하여야 합니다.")
        String nickname
) {
}
