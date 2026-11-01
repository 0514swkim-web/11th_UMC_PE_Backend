package com.umc.study.exception;

// 요청한 자원(카테고리, 도서, 대여 기록 등)이 없을 때 → 404
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
