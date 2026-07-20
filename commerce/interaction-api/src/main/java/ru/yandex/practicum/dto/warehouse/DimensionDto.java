package ru.yandex.practicum.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Builder
@Getter
@Service
@AllArgsConstructor
@NoArgsConstructor
public class DimensionDto {
    private Double width;
    private Double height;
    private Double depth;
}
