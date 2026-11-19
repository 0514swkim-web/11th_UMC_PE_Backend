package com.umc.movie.dto;

import com.umc.movie.entity.Favorite;

import java.time.LocalDateTime;

// 즐겨찾기 응답: 엔티티 대신 필요한 값만 옮겨 담음 (member를 그대로 내보내지 않기)
public record FavoriteResponse(
        Long favoriteId,
        Long movieId,
        LocalDateTime createdAt
) {
    public static FavoriteResponse from(Favorite favorite) {
        return new FavoriteResponse(
                favorite.getFavoriteId(),
                favorite.getMovieId(),
                favorite.getCreatedAt()
        );
    }
}
