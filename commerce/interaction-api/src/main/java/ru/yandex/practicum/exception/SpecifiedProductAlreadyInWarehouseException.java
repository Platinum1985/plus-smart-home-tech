package ru.yandex.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SpecifiedProductAlreadyInWarehouseException extends RuntimeException {
    private HttpStatus httpStatus;
    private String userMessage;

    public SpecifiedProductAlreadyInWarehouseException(String message) {
        super(message);
        this.httpStatus = HttpStatus.CONFLICT;
        this.userMessage = message;
    }

    public SpecifiedProductAlreadyInWarehouseException(String message, String userMessage) {
        super(message);
        this.httpStatus = HttpStatus.CONFLICT;
        this.userMessage = userMessage;
    }
}
