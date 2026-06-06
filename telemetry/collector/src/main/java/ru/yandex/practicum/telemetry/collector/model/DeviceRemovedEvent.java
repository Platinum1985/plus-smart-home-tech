package ru.yandex.practicum.telemetry.collector.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeviceRemovedEvent extends HubEvent {

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
