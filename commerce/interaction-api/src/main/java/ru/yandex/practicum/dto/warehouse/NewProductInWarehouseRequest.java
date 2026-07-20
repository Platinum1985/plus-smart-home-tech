package ru.yandex.practicum.dto.warehouse;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewProductInWarehouseRequest {
    private UUID productId;
    private Boolean fragile;
    private DimensionDto dimension;
    private Double weight;
}
