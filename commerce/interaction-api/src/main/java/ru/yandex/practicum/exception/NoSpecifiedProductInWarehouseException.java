package ru.yandex.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NoSpecifiedProductInWarehouseException extends RuntimeException {
  private HttpStatus httpStatus;
  private String userMessage;

  public NoSpecifiedProductInWarehouseException(String message) {
    super(message);
    this.httpStatus = HttpStatus.NOT_FOUND;
    this.userMessage = message;
  }

  public NoSpecifiedProductInWarehouseException(String message, String userMessage) {
    super(message);
    this.httpStatus = HttpStatus.NOT_FOUND;
    this.userMessage = userMessage;
  }
}