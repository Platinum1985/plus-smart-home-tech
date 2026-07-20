package ru.yandex.practicum.dto.warehouse;

import lombok.*;
import org.springframework.stereotype.Service;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DimensionDto {
    private Double width;
    private Double height;
    private Double depth;
}
