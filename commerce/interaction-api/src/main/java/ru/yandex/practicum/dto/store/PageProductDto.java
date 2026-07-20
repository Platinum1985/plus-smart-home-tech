package ru.yandex.practicum.dto.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Builder
@Getter
@Service
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
