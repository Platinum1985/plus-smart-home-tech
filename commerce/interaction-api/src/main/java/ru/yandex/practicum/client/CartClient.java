package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.cart.entity.ShoppingCart;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface CartClient {

    @GetMapping
    ShoppingCartDto getActualCart(
            @RequestParam String username
    );

    @PutMapping
    ShoppingCartDto addProductFromCart(
            @RequestParam String username,
            @RequestBody Map<UUID, Long> productIds
            );

    @DeleteMapping
    void removeCart(
            @RequestParam String username
    );

    @PostMapping("/remove")
    ShoppingCartDto clearProductFromCart(
            @RequestParam String username,
            @RequestBody List<UUID> productIds
    );

    @PostMapping("/change-quantity")
    ShoppingCartDto changeQuantityProductFromCart(
            @RequestParam String username,
            @RequestBody ChangeProductQuantityRequest dto
    );

    @GetMapping("/{shoppingCartId}")
    ShoppingCart getCartById(@PathVariable UUID shoppingCartId);

}
