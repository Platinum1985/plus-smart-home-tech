package ru.yandex.practicum.dto.warehouse;

import lombok.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddProductToWarehouseRequest {
    private UUID productId;
    private Integer quantity;
}
