package ru.yandex.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NoDeliveryFoundException extends RuntimeException {
    private HttpStatus httpStatus;
    private String userMessage;

    public NoDeliveryFoundException(String message) {
        super(message);
        this.httpStatus = HttpStatus.NOT_FOUND;
        this.userMessage = message;
    }

    public NoDeliveryFoundException(String message, String userMessage) {
        super(message);
        this.httpStatus = HttpStatus.NOT_FOUND;
        this.userMessage = userMessage;
    }
}
