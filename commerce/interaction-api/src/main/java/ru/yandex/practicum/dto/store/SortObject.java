package ru.yandex.practicum.dto.store;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SortObject {
    private String direction;
    private String nullHandling;
    private Boolean ascending;
    private String property;
    private Boolean ignoreCase;
}
