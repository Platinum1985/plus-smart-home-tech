package ru.yandex.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ProductInShoppingCartLowQuantityInWarehouse extends RuntimeException {
    private HttpStatus httpStatus;
    private String userMessage;

    public ProductInShoppingCartLowQuantityInWarehouse(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.userMessage = message;
    }

    public ProductInShoppingCartLowQuantityInWarehouse(String message, String userMessage) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.userMessage = userMessage;
    }
}
