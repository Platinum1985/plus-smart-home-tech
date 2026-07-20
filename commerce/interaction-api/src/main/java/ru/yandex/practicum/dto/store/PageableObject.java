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
public class PageableObject {
    private Long offset;
    private List<SortObject> sort;
    private Boolean unpaged;
    private Boolean paged;
    private Integer pageNumber;
    private Integer pageSize;
}
