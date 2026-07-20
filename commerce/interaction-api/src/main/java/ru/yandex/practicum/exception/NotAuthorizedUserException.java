package ru.yandex.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotAuthorizedUserException extends RuntimeException {
    private HttpStatus httpStatus;
    private String userMessage;

    public NotAuthorizedUserException(String message) {
        super(message);
        this.httpStatus = HttpStatus.UNAUTHORIZED;
        this.userMessage = message;
    }

    public NotAuthorizedUserException(String message, String userMessage) {
        super(message);
        this.httpStatus = HttpStatus.UNAUTHORIZED;
        this.userMessage = userMessage;
    }
}
