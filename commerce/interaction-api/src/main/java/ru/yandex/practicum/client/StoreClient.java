package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.store.PageProductDto;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface StoreClient {

    @GetMapping
    PageProductDto getProductByCategory(
            @RequestParam String category,
            @RequestParam Integer page,
            @RequestParam Integer size,
            @RequestParam List<String> sort
    );

    @PutMapping
    ProductDto createNewProduct(@RequestBody ProductDto dto);

    @PostMapping
    ProductDto updateProduct(@RequestBody ProductDto dto);

    @PostMapping("/removeProductFromStore")
    boolean removeProduct(@RequestBody UUID productId);

    @PostMapping("/quantityState")
    boolean quantityStateByProduct(@RequestBody SetProductQuantityStateRequest request);

    @GetMapping("/{productId}")
    ProductDto getProductById(@PathVariable UUID productId);
}