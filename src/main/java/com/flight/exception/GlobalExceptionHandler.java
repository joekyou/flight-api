package com.flight.exception;

import com.flight.dto.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleException(Exception e) {
        e.printStackTrace(); // For debugging
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new BaseResponse<>(500, e.getMessage(), null));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse<?>> handleRuntimeException(RuntimeException e) {
        e.printStackTrace(); // For debugging
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new BaseResponse<>(400, e.getMessage(), null));
    }
}
