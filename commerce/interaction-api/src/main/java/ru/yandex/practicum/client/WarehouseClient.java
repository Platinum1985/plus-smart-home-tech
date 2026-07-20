package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient {
    @PutMapping
    void addNewProduct(@RequestBody NewProductInWarehouseRequest dto);

    @PostMapping("/check")
    BookedProductsDto checkProduct(@RequestBody ShoppingCartDto dto);

    @PostMapping("/add")
    void addProduct(@RequestBody AddProductToWarehouseRequest dto);

    @GetMapping("/address")
    AddressDto getAddress();

    @PostMapping("/assembly")
    BookedProductsDto assemblyProductsForOrder(@RequestBody AssemblyProductsForOrderRequest assemblyProduct);

    @GetMapping("/warehouse-address")
    AddressDto getWarehouseAddress();

    @PostMapping("/shipped")
    void shippedToDelivery(@RequestBody ShippedToDeliveryRequest shippedToDeliveryRequest);

    @PostMapping("/return")
    void acceptReturn(@RequestBody Map<UUID, Long> products);

}
