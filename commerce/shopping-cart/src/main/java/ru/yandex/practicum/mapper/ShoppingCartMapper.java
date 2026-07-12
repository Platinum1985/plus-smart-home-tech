package ru.yandex.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.cart.entity.CartItem;
import ru.yandex.practicum.dto.cart.entity.ShoppingCart;

import java.util.stream.Collectors;

@UtilityClass
public class ShoppingCartMapper {

    public ShoppingCartDto toDto(ShoppingCart shoppingCart) {
        return ShoppingCartDto.builder()
                .shoppingCartId(shoppingCart.getShoppingCartId())
                .products(shoppingCart.getItems().stream()
                        .collect(Collectors.toMap(
                                        CartItem::getProductId,
                                        CartItem::getQuantity )
                        )
                ).build();
    }

}
