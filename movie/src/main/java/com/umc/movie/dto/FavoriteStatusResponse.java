package com.umc.movie.dto;

// 특정 영화 즐겨찾기 여부: 안 했어도 오류가 아니라 정상 상태 → 200 + false
public record FavoriteStatusResponse(
        boolean isFavorite
) {
}
