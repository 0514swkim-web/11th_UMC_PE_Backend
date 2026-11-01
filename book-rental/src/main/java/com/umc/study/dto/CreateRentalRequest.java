package com.umc.study.dto;

import jakarta.validation.constraints.NotNull;

public record CreateRentalRequest(
        @NotNull(message = "userId는 필수입니다.")
        Long userId,

        @NotNull(message = "bookId는 필수입니다.")
        Long bookId
) {
}
