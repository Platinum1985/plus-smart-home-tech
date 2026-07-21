package ru.yandex.practicum.dto.cart;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeProductQuantityRequest {
    private UUID productId;
    private Long newQuantity;
}
