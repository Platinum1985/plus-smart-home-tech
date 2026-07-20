package ru.yandex.practicum.dto.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.state.QuantityState;

import java.util.UUID;

@Getter
@Builder
@Service
@AllArgsConstructor
@NoArgsConstructor
public class SetProductQuantityStateRequest {
    private UUID productId;
    private QuantityState quantityState;
}
