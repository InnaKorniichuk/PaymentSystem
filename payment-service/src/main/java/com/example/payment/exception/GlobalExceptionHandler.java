package com.example.payment.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("BAD_REQUEST_OR_NOT_FOUND: {}", ex.getMessage());

        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "error", "Not Found or Access Denied",
                "message", ex.getMessage(),
                "status", HttpStatus.NOT_FOUND.value()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}