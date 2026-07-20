package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.dto.warehouse.entity.WarehouseProduct;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.repository.WarehouseAddressRepository;
import ru.yandex.practicum.repository.WarehouseProductRepository;

import java.time.Instant;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseProductRepository warehouseProductRepository;
    private final WarehouseAddressRepository warehouseAddressRepository;

    private static final String ADDRESS = "address";

    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        // TODO: реализовать добавление нового товара на склад

        if (warehouseProductRepository.existsByProductId(request.getProductId())) {
            log.debug("Продукт c id({}) уже есть на складе", request.getProductId());
            throw new SpecifiedProductAlreadyInWarehouseException("Продукт уже есть на складе");
        }

        DimensionDto dimension = request.getDimension();
        WarehouseProduct product = WarehouseProduct.builder()
                .productId(request.getProductId())
                .quantity(0L)
                .fragile(request.getFragile())
                .weight(request.getWeight())
                .width(dimension.getWidth())
                .height(dimension.getHeight())
                .depth(dimension.getDepth())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        log.info("Продукт склада собран");
        WarehouseProduct savedProduct = warehouseProductRepository.save(product);
        log.info("Продукт склада сохранён id:{}", savedProduct.getProductId());
    }

    /**
     * Передача товаров в доставку.
     */
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        log.info("Передача товаров в доставку для заказа: {}, доставка: {}",
                request.getOrderId(), request.getDeliveryId());
        // TODO: Реализовать передачу товаров в доставку
        // Обновить статус товаров на "переданы в доставку"
        // Уменьшить количество зарезервированных товаров

        // хз что тут должно быть
    }

    /**
     * Принять возврат товаров на склад.
     */
    public void acceptReturn(Map<UUID, Long> products) {
        log.info("Принятие возврата товаров на склад: {} товаров", products.size());
        // TODO: Реализовать приём возврата товаров
        Set<UUID> productIds = products.keySet();
        List<WarehouseProduct> warehouseProducts = warehouseProductRepository.findByProductIdIn(productIds);

        // Увеличение количества товаров на складе
        List<WarehouseProduct> updatedProducts = warehouseProducts.stream().peek(product -> {
            product.setQuantity(product.getQuantity() + products.get(product.getProductId()));
            product.setUpdatedAt(Instant.now());
        }).toList();

        warehouseProductRepository.saveAll(updatedProducts); // Сохранение изменений

        log.info("Товары вернулись на склад");
    }

    @Transactional
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        // TODO: проверить наличие товаров и вернуть зарезервированные данные
        log.info("Сборка товаров для заказа: {}", request.getOrderId());
        Set<UUID> productIds = request.getProducts().keySet();
        List<WarehouseProduct> products = warehouseProductRepository.findByProductIdIn(productIds);

        // Проверка товара на складе
        BookedProductsDto bookedProductsDto = checkProductQuantityEnoughForShoppingCart(
                ShoppingCartDto.builder()
                        .products(request.getProducts())
                        .build()
        );

        // Резервирование товара
        List<WarehouseProduct> updatedProducts = products.stream().peek(product -> {
            product.setQuantity(product.getQuantity() - request.getProducts().get(product.getProductId()));
            product.setUpdatedAt(Instant.now());
        }).toList();

        warehouseProductRepository.saveAll(updatedProducts); // Сохранение изменений

        log.info("Товары зарезервированы");

        return bookedProductsDto;
    }

    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        // TODO: реализовать прием товара на склад

        Optional<WarehouseProduct> found = warehouseProductRepository.findByProductId(request.getProductId());;
        if (found.isEmpty()) {
            log.debug("Продукта c id({}) нет на складе и его нельзя добавить", request.getProductId());
            throw new SpecifiedProductAlreadyInWarehouseException("Продукт нет на складе, нельзя добавить не зарегистрированный товар");
        } else {
            WarehouseProduct product = found.get();
            log.info("Количество товара перед обновлением: {}", product.getQuantity());
            product.setQuantity(product.getQuantity() + request.getQuantity()); // Добавляем продукт к имеющемуся количеству
            product.setUpdatedAt(Instant.now());
            WarehouseProduct updatedProduct = warehouseProductRepository.save(product); // Сохранение изменений
            log.info("Количество товара обновлено: id:{}, quantity:{}", updatedProduct.getProductId(), updatedProduct.getQuantity());
        }

    }

    public AddressDto getWarehouseAddress() {
        // TODO: вернуть адрес склада
        return AddressDto.builder()
                .country(ADDRESS)
                .city(ADDRESS)
                .street(ADDRESS)
                .house(ADDRESS)
                .flat(ADDRESS)
                .build();
    }

    private double calculateTotalVolume(List<WarehouseProduct> products, Map<UUID, Long> productQuantity) {
        // TODO: рассчитать объем товара
        double result = 0.0;
        for (WarehouseProduct product : products) {
            long quantity = productQuantity.get(product.getProductId()); // Количество товаров одного вида
            result += quantity *
                    (product.getWidth() * product.getHeight() * product.getDepth());
        }
        return result;
    }

    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCart) {
        Set<UUID> productIds = shoppingCart.getProducts().keySet();
        List<WarehouseProduct> products = warehouseProductRepository.findByProductIdIn(productIds);

        products.forEach(product -> {
            if (
                    product.getQuantity() < shoppingCart.getProducts().get(product.getProductId()) // Проверяем хватает ли количества продуктов
            )
                throw new ProductInShoppingCartLowQuantityInWarehouse("Запрашиваемых продуктов больше чем есть на складе");
        });

        // Расчёт веса заказа
        return BookedProductsDto.builder()
                .deliveryWeight(products.stream().map(WarehouseProduct::getWeight).reduce(0.0, Double::sum)) // Получение суммы весов продуктов
                .deliveryVolume(calculateTotalVolume(products, shoppingCart.getProducts())) // Расчёт общего объёма груза
                .fragile(products.stream().anyMatch(WarehouseProduct::isFragile)) // Если есть хотя бы один хрупкий груз вернуть true
                .build();
    }
}