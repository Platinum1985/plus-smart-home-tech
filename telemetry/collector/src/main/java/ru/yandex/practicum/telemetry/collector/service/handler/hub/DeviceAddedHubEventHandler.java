package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.KafkaClient;
import ru.yandex.practicum.telemetry.collector.model.DeviceAddedEvent;
import ru.yandex.practicum.telemetry.collector.model.HubEventType;
import ru.yandex.practicum.telemetry.collector.utils.EnumMapper;

@Component
public class DeviceAddedHubEventHandler extends BaseHubEventHandler<DeviceAddedEvent> {

    public DeviceAddedHubEventHandler(KafkaClient producer) {
        super(producer);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_ADDED;
    }

    @Override
    protected HubEventAvro mapToAvro(DeviceAddedEvent event) {
        DeviceAddedEventAvro avroPayload = DeviceAddedEventAvro.newBuilder()
                .setId(event.getId())
                .setType(EnumMapper.map(event.getDeviceType(), DeviceTypeAvro.class))
                .build();
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(avroPayload)
                .build();
    }
}
