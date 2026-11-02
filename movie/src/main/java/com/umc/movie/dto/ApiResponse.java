package com.umc.movie.dto;

// 성공·실패 모두 같은 모양으로 감싸서 보내는 공통 응답
public record ApiResponse<T>(boolean isSuccess, String code, String message, T result) {

    public static <T> ApiResponse<T> created(String message, T result) {
        return new ApiResponse<>(true, "COMMON201", message, result);
    }

    public static <T> ApiResponse<T> ok(String message, T result) {
        return new ApiResponse<>(true, "COMMON200", message, result);
    }

    public static ApiResponse<Void> failure(String code, String message) {
        return new ApiResponse<>(false, code, message, null); // 실패는 돌려줄 데이터가 없어서 null
    }
}
