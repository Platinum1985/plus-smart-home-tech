package ru.yandex.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotEnoughInfoInOrderToCalculateException extends RuntimeException {
    private HttpStatus httpStatus;
    private String userMessage;

    public NotEnoughInfoInOrderToCalculateException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.userMessage = message;
    }

    public NotEnoughInfoInOrderToCalculateException(String message, String userMessage) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.userMessage = userMessage;
    }
}