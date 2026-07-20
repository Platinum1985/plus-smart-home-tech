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
public class AddProductToWarehouseRequest {
    private UUID productId;
    private Integer quantity;
}
