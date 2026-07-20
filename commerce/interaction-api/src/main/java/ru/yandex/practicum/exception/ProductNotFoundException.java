package ru.yandex.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ProductNotFoundException extends RuntimeException {
    private HttpStatus httpStatus;
    private String userMessage;

    public ProductNotFoundException(String message) {
        super(message);
        this.httpStatus = HttpStatus.NOT_FOUND;
        this.userMessage = message;
    }

    public ProductNotFoundException(String message, String userMessage) {
        super(message);
        this.httpStatus = HttpStatus.NOT_FOUND;
        this.userMessage = userMessage;
    }
}
