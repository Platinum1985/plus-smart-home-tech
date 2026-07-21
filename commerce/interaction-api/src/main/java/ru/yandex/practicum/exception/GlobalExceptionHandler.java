package ru.yandex.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotAuthorizedUserException.class)
    public ResponseEntity<ErrorResponse> handleNotAuthorizedUser(NotAuthorizedUserException ex) {
        log.error("Ошибка авторизации: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .userMessage(ex.getUserMessage())
                .message(ex.getMessage())
                .stackTrace(getStackTraceAsString(ex))
                .cause(getCauseAsString(ex))
                .localizedMessage(ex.getLocalizedMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(NoProductsInShoppingCartException.class)
    public ResponseEntity<ErrorResponse> handleNoProductsInShoppingCart(NoProductsInShoppingCartException ex) {
        log.error("Товары не найдены в корзине: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .userMessage(ex.getUserMessage())
                .message(ex.getMessage())
                .stackTrace(getStackTraceAsString(ex))
                .cause(getCauseAsString(ex))
                .localizedMessage(ex.getLocalizedMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException ex) {
        log.error("Товар не найден: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .userMessage(ex.getUserMessage())
                .message(ex.getMessage())
                .stackTrace(getStackTraceAsString(ex))
                .cause(getCauseAsString(ex))
                .localizedMessage(ex.getLocalizedMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    public ResponseEntity<ErrorResponse> handleNoSpecifiedProductInWarehouse(NoSpecifiedProductInWarehouseException ex) {
        log.error("Указанный товар отсутствует на складе: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .userMessage(ex.getUserMessage())
                .message(ex.getMessage())
                .stackTrace(getStackTraceAsString(ex))
                .cause(getCauseAsString(ex))
                .localizedMessage(ex.getLocalizedMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ProductInShoppingCartLowQuantityInWarehouse.class)
    public ResponseEntity<ErrorResponse> handleProductLowQuantityInWarehouse(ProductInShoppingCartLowQuantityInWarehouse ex) {
        log.error("Недостаточное количество товара на складе: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .userMessage(ex.getUserMessage())
                .message(ex.getMessage())
                .stackTrace(getStackTraceAsString(ex))
                .cause(getCauseAsString(ex))
                .localizedMessage(ex.getLocalizedMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class)
    public ResponseEntity<ErrorResponse> handleSpecifiedProductAlreadyInWarehouse(SpecifiedProductAlreadyInWarehouseException ex) {
        log.error("Товар уже существует на складе: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .statusCode(HttpStatus.CONFLICT.value())
                .userMessage(ex.getUserMessage())
                .message(ex.getMessage())
                .stackTrace(getStackTraceAsString(ex))
                .cause(getCauseAsString(ex))
                .localizedMessage(ex.getLocalizedMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(NoDeliveryFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoDeliveryFound(NoDeliveryFoundException ex) {
        log.error("Доставка не найдена: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .userMessage(ex.getUserMessage())
                .message(ex.getMessage())
                .stackTrace(getStackTraceAsString(ex))
                .cause(getCauseAsString(ex))
                .localizedMessage(ex.getLocalizedMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(NoOrderFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoOrderFound(NoOrderFoundException ex) {
        log.error("Заказ не найден: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .userMessage(ex.getUserMessage())
                .message(ex.getMessage())
                .stackTrace(getStackTraceAsString(ex))
                .cause(getCauseAsString(ex))
                .localizedMessage(ex.getLocalizedMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(NotEnoughInfoInOrderToCalculateException.class)
    public ResponseEntity<ErrorResponse> handleNotEnoughInfoInOrderToCalculate(NotEnoughInfoInOrderToCalculateException ex) {
        log.error("Недостаточно информации в заказе для расчёта: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .userMessage(ex.getUserMessage())
                .message(ex.getMessage())
                .stackTrace(getStackTraceAsString(ex))
                .cause(getCauseAsString(ex))
                .localizedMessage(ex.getLocalizedMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Некорректные параметры запроса: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .userMessage("Некорректные параметры запроса")
                .message(ex.getMessage())
                .stackTrace(getStackTraceAsString(ex))
                .cause(getCauseAsString(ex))
                .localizedMessage(ex.getLocalizedMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Непредвиденная ошибка: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .userMessage("Внутренняя ошибка сервера")
                .message(ex.getMessage())
                .stackTrace(getStackTraceAsString(ex))
                .cause(getCauseAsString(ex))
                .localizedMessage(ex.getLocalizedMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    private String getStackTraceAsString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    private String getCauseAsString(Throwable throwable) {
        if (throwable.getCause() != null) {
            return throwable.getCause().toString();
        }
        return null;
    }
}