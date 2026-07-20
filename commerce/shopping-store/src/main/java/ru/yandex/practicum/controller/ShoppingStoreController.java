package ru.yandex.practicum.controller;

import jakarta.servlet.ServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.store.PageProductDto;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.service.ShoppingStoreService;
import ru.yandex.practicum.state.QuantityState;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController {

    private final ShoppingStoreService shoppingStoreService;

    /**
     * Получение страницы товаров указанной категории
     */
    @GetMapping
    public PageProductDto getProducts(
            @RequestParam String category,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String sort
    ) {
        if (sort == null || sort.isEmpty()) {
            sort = "productName,asc";
        }

        return shoppingStoreService.getProducts(category, page, size, sort);
    }

    /**
     * Создание нового товара
     */
    @PutMapping
    public ProductDto createNewProduct(@RequestBody ProductDto productDto) {
        return shoppingStoreService.createNewProduct(productDto);
    }

    /**
     * Обновление товара
     */
    @PostMapping
    public ProductDto updateProduct(@RequestBody ProductDto productDto) {
        return shoppingStoreService.updateProduct(productDto);
    }

    /**
     * Удаление товара из ассортимента
     */
    @PostMapping("/removeProductFromStore")
    public boolean removeProductFromStore(@RequestBody UUID productId) {
        return shoppingStoreService.removeProductFromStore(productId);
    }

    /**
     * Установка статуса количества товара
     */
    @PostMapping("/quantityState")
    public boolean setProductQuantityState(
            @RequestParam UUID productId,
            @RequestParam QuantityState quantityState
    ) {
        return shoppingStoreService.setProductQuantityState(SetProductQuantityStateRequest.builder()
                .productId(productId)
                .quantityState(quantityState)
                .build());
    }

    /**
     * Получение стоимости товаров по идентификаторам
     */
    @GetMapping("/price")
    public Double getProductPrice(@RequestBody Map<UUID, Long> productIdsAndQuantity) {
        return shoppingStoreService.getProductPrice(productIdsAndQuantity);
    }

    /**
     * Получение товара по идентификатору
     */
    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable UUID productId) {
        return shoppingStoreService.getProduct(productId);
    }

}