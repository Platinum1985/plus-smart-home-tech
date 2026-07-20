package ru.yandex.practicum.dto.store;

import lombok.*;
import ru.yandex.practicum.state.QuantityState;

import java.util.UUID;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SetProductQuantityStateRequest {
    private UUID productId;
    private QuantityState quantityState;
}
