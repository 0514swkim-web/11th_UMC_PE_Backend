package com.umc.movie.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// 즐겨찾기 등록 요청: 영화 ID만 받음 (누구인지는 토큰으로 판단하니까 memberId는 받지 않음)
public record CreateFavoriteRequest(
        @Schema(description = "TMDB 영화 ID", example = "550")
        @NotNull(message = "영화 ID는 필수입니다.")
        @Positive(message = "영화 ID는 양수여야 합니다.")
        Long movieId
) {
}
