package com.umc.study.dto;

import com.umc.study.entity.Book;

// Entity 대신 API로 내보낼 모양만 담는 응답 DTO (DB 컬럼명이 아니라 camelCase)
public record BookResponse(
        Long bookId,
        String title,
        String description,
        String categoryName,
        Boolean isAvailable
) {
    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getBookId(),
                book.getTitle(),
                book.getDescription(),
                book.getCategory().getName(),
                book.getIsAvailable()
        );
    }
}
