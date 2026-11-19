package com.umc.movie.dto;

// 영화 기록 응답: addToFavorites가 false면 favorite은 null
public record MovieRecordResponse(
        RatingResponse rating,
        FavoriteResponse favorite
) {
}
