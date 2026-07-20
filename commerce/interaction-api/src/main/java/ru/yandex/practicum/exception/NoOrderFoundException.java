package ru.yandex.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NoOrderFoundException extends RuntimeException {
    private HttpStatus httpStatus;
    private String userMessage;

    public NoOrderFoundException(String message) {
        super(message);
        this.httpStatus = HttpStatus.NOT_FOUND;
        this.userMessage = message;
    }

    public NoOrderFoundException(String message, String userMessage) {
        super(message);
        this.httpStatus = HttpStatus.NOT_FOUND;
        this.userMessage = userMessage;
    }
}