package io.goorm.youtube.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ErrorResponse {
    private String message;
    private Map<String, String> errors;
    private LocalDateTime timestamp;

    public ErrorResponse(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String message, Map<String, String> errors) {
        this.message = message;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }
}

