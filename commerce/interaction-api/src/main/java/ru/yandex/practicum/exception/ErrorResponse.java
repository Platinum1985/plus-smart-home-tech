package ru.yandex.practicum.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private HttpStatus httpStatus;
    private int statusCode;
    private String userMessage;
    private String message;
    private String stackTrace;
    private String cause;
    private String localizedMessage;

}
