package ru.yandex.practicum.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Builder
@Getter
@Service
@AllArgsConstructor
@NoArgsConstructor
public class NewProductInWarehouseRequest {
    private UUID productId;
    private Boolean fragile;
    private DimensionDto dimension;
    private Double weight;
}
