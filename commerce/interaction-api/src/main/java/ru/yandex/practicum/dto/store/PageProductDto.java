package ru.yandex.practicum.dto.store;

import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageProductDto {
    private Long totalElements;
    private Integer totalPages;
    private Boolean first;
    private Boolean last;
    private Integer size;
    private List<ProductDto> content;
    private Integer number;
    private List<SortObject> sort;
    private Integer numberOfElements;
    private PageableObject pageable;
    private Boolean empty;
}
