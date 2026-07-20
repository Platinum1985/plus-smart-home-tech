package ru.yandex.practicum.dto.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.state.ProductCategory;
import ru.yandex.practicum.state.ProductState;
import ru.yandex.practicum.state.QuantityState;

import java.util.UUID;

@Builder
@Getter
@Service
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private UUID productId;
    private String productName;
    private String description;
    private String imageSrc;
    private QuantityState quantityState;
    private ProductState productState;
    private ProductCategory productCategory;
    private Double price;
}
