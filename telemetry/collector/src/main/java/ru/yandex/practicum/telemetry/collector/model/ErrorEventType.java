package ru.yandex.practicum.telemetry.collector.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
class ErrorEventType extends HubEvent {

    private Object rawData; // исходные данные, которые не удалось десериализовать

    @JsonIgnore
    @Override
    public HubEventType getType() {
        return null; // или специальный тип ERROR
    }
}
