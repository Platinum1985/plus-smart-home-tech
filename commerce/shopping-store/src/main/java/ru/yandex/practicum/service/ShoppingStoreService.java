package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.store.PageProductDto;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.dto.store.entity.Product;
import ru.yandex.practicum.dto.warehouse.DimensionDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.repository.ProductRepository;
import ru.yandex.practicum.state.ProductCategory;
import ru.yandex.practicum.state.ProductState;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingStoreService {

    private final ProductRepository productRepository;
    private final WarehouseClient warehouseClient;

    public PageProductDto getProducts(String category, Integer page, Integer size, String sort) {
        // Преобразуем строку категории в enum
        ProductCategory productCategory;
        try {
            productCategory = ProductCategory.valueOf(category);
        } catch (IllegalArgumentException e) {
            log.error("Invalid category: {}", category);
            throw new IllegalArgumentException("Invalid category: " + category);
        }

        // Создаем объект Sort из параметров сортировки
        Sort sortObj = buildSort(sort);

        // Создаем Pageable объект
        Pageable pageable = PageRequest.of(page, size, sortObj);

        // Получаем страницу товаров
        Page<Product> productPage = productRepository.findByProductCategory(
                productCategory,
                pageable
        );

        // Конвертируем Page<Product> в PageProductDto
        return ProductMapper.toPageDto(productPage);
    }

    @Transactional
    public ProductDto createNewProduct(ProductDto dto) {
        // TODO: реализовать создание нового товара
        Product product = Product.builder()
                .productId(UUID.randomUUID())
                .productName(dto.getProductName())
                .description(dto.getDescription())
                .imageSrc(dto.getImageSrc())
                .quantityState(dto.getQuantityState())
                .productState(dto.getProductState())
                .productCategory(dto.getProductCategory())
                .price(dto.getPrice())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        Product savedProduct = productRepository.save(product);
        log.info("Продукт создан и сохранён: {}", product);

        // Дефолтное создание запроса на добавление товара на склад (возможно временно)
        NewProductInWarehouseRequest request = NewProductInWarehouseRequest.builder()
                .productId(savedProduct.getProductId())
                .weight(0.0)
                .dimension(DimensionDto.builder()
                        .depth(0.0)
                        .height(0.0)
                        .width(0.0)
                        .build())
                .fragile(false)
                .build();
        log.info("Отправка запроса на добавление товара на склад");
        warehouseClient.addNewProduct(request);


        return ProductMapper.toDto(savedProduct);
    }

    @Transactional
    public ProductDto updateProduct(ProductDto dto) {
        // TODO: реализовать обновление товара

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Товар не найден на витрине"));

        Product updatingProduct = Product.builder()
                .productId(dto.getProductId())
                .productName(dto.getProductName() == null ? product.getProductName() : dto.getProductName())
                .description(dto.getDescription() == null ? product.getDescription() : dto.getDescription())
                .imageSrc(dto.getImageSrc() == null ? product.getImageSrc() : dto.getImageSrc())
                .quantityState(dto.getQuantityState() == null ? product.getQuantityState() : dto.getQuantityState())
                .productState(dto.getProductState() == null ? product.getProductState() : dto.getProductState())
                .productCategory(dto.getProductCategory() == null ? product.getProductCategory() : dto.getProductCategory())
                .price(dto.getPrice() == null ? product.getPrice() : dto.getPrice())
                .createdAt(product.getCreatedAt())
                .updatedAt(Instant.now())
                .build();
        Product savedProduct = productRepository.save(updatingProduct);
        log.info("Продукт обновлён");

        return ProductMapper.toDto(savedProduct);
    }

    @Transactional
    public boolean removeProductFromStore(UUID productId) {
        // TODO: реализовать удаление товара
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Товар не найден на витрине"));

            product.setProductState(ProductState.DEACTIVATE);
            product.setUpdatedAt(Instant.now());
            Product savedProduct = productRepository.save(product);
            log.info("Статус товара успешно изменён на 'DEACTIVATE'");

            return true;
        } catch (ProductNotFoundException e) {
            log.debug("Произошла ошибка, товар не получилось удалить");
            return false;
        }
    }

    @Transactional
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        // TODO: реализовать установку статуса количества
        try {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("Товар не найден на витрине"));

            product.setQuantityState(request.getQuantityState());
            product.setUpdatedAt(Instant.now());
            Product savedProduct = productRepository.save(product);
            log.info("Статус товара успешно изменён на '{}'", request.getQuantityState());

            return true;
        } catch (ProductNotFoundException e) {
            log.debug("Произошла ошибка, статус количества товара не получилось обновить");
            return false;
        }
    }

    public Double getProductPrice(Map<UUID, Long> productIdsAndQuantity) {
        Set<UUID> productIds = productIdsAndQuantity.keySet();
        List<Product> products = productRepository.findAllById(productIds);

        // Подсчет общей суммы
        Double totalPrice = products.stream()
                .map(product -> product.getPrice() * productIdsAndQuantity.getOrDefault(product.getProductId(), 0L))
                .reduce(0.0, Double::sum);

        return totalPrice;
    }

    public ProductDto getProduct(UUID productId) {
        // TODO: реализовать получение товара по ID
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Товар не найден на витрине"));
        return ProductMapper.toDto(product);
    }

    private Sort buildSort(String sortParam) {
        if (sortParam == null || sortParam.trim().isEmpty()) {
            return Sort.by(Sort.Direction.ASC, "productName");
        }

        String[] parts = sortParam.split(",");
        String property = parts[0].trim();
        Sort.Direction direction = Sort.Direction.ASC;

        if (parts.length > 1) {
            String directionStr = parts[1].trim().toLowerCase();
            if ("desc".equals(directionStr)) {
                direction = Sort.Direction.DESC;
            } else if (!"asc".equals(directionStr)) {
                log.warn("Invalid sort direction: {}, using ASC", directionStr);
            }
        }

        return Sort.by(direction, property);
    }
}