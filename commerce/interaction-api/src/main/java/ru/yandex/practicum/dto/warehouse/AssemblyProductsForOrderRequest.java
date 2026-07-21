package ru.yandex.practicum.dto.warehouse;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssemblyProductsForOrderRequest {

    private Map<UUID, Long> products;
    private UUID orderId;

}