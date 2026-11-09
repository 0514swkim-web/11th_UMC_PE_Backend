package com.umc.movie.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// 상태 코드 + code 문자열 + 메시지를 한 번에 들고 던지는 예외
// 어디서 던지든 GlobalExceptionHandler가 받아서 ApiResponse 실패 형식으로 바꿔 줌
@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus status; // 응답 HTTP 상태 코드 (401, 403, 404, 409 ...)
    private final String code;       // 응답 바디 code 필드 (AUTH401_1, RATING403_1 ...)

    public ApiException(HttpStatus status, String code, String message) {
        super(message); // message는 부모 RuntimeException이 들고 있음 → getMessage()
        this.status = status;
        this.code = code;
    }
}
