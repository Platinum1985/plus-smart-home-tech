package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.cart.entity.CartItem;
import ru.yandex.practicum.dto.cart.entity.ShoppingCart;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.repository.CartItemRepository;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final WarehouseClient warehouseClient;

    public ShoppingCartDto getShoppingCart(String username) {
        // TODO: реализовать получение корзины
        ShoppingCart cart = getOrCreateActiveCart(username);
        return ShoppingCartMapper.toDto(cart);
    }

    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Long> products) {
        // TODO: реализовать добавление товаров в корзину
        ShoppingCart cart = getOrCreateActiveCart(username);

        // Создание новых ItemCart
        List<CartItem> items = products.entrySet().stream()
                .map(entry -> createNewCartItem(cart, entry.getKey(), entry.getValue()))
                .toList();

        // Добавление новых ItemCart
        List<CartItem> oldItems = cart.getItems();
        oldItems.addAll(items);
        cart.setItems(oldItems);

        cart.setUpdatedAt(Instant.now()); // Обновление времени

        cartItemRepository.saveAll(items);
        ShoppingCart updatedCart = shoppingCartRepository.save(cart); // Сохранение изменений

        return ShoppingCartMapper.toDto(updatedCart);
    }

    public void deactivateCurrentShoppingCart(String username) {
        // TODO: реализовать деактивацию корзины
        ShoppingCart cart = getOrCreateActiveCart(username);

        cart.setActive(false);
        cart.setUpdatedAt(Instant.now());

        ShoppingCart updatingCart = shoppingCartRepository.save(cart);
        log.info("Корзина деактивирована {}", updatingCart);
    }

    @Transactional
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> productIds) {
        // TODO: реализовать удаление товаров из корзины
        ShoppingCart cart = getOrCreateActiveCart(username);

        cartItemRepository.deleteByShoppingCart_ShoppingCartIdAndProductIdIn(cart.getShoppingCartId(), productIds); // Удаление продуктов из корзины в бд cartItem
        ShoppingCart updatedCart = shoppingCartRepository.save(cart); // Сохранение изменений корзины в бд корзины

        return ShoppingCartMapper.toDto(updatedCart);
    }

    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        // TODO: реализовать изменение количества товара
        ShoppingCart cart = getOrCreateActiveCart(username);

        // Проверка наличия товара в корзине
        boolean exists = cart.getItems().stream()
                .anyMatch(item -> item.getProductId().equals(request.getProductId()));

        if (!exists) throw new ProductNotFoundException("Продукт не найден в корзине");

        // Изменение количества
        cart.getItems().stream()
                .filter(cartItem -> cartItem.getProductId().equals(request.getProductId()))
                .findFirst()
                .ifPresent(cartItem -> cartItem.setQuantity(request.getNewQuantity()));

        cart.setUpdatedAt(Instant.now()); // Обновление времени
        ShoppingCart updatedCart = shoppingCartRepository.save(cart); // Сохранение изменений

        return ShoppingCartMapper.toDto(updatedCart);
    }

    private ShoppingCart getOrCreateActiveCart(String username) {
        // TODO: получить активную корзину или создать новую
        try {
            ShoppingCart cart = shoppingCartRepository.findByUsernameAndActiveTrue(username)
                    .orElseThrow(() -> new ProductNotFoundException("Корзина не найдена"));
            log.info("Найдена активная корзина");
            return cart;
        }catch (ProductNotFoundException e) {
            ShoppingCart newCart = ShoppingCart.builder()
                    .shoppingCartId(UUID.randomUUID())
                    .username(username)
                    .active(true)
                    .items(new ArrayList<>())
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            ShoppingCart savedCart = shoppingCartRepository.save(newCart);
            log.info("Создана новая корзина");
            return savedCart;
        }
    }

    private CartItem createNewCartItem(ShoppingCart cart, UUID productId, Long quantity) {
        return CartItem.builder()
                .shoppingCart(cart)
                .productId(productId)
                .quantity(quantity)
                .build();
    }

}