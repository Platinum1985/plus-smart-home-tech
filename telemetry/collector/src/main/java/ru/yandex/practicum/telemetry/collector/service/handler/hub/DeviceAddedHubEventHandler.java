package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.apache.avro.generic.GenericRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.collector.model.DeviceAddedEvent;
import ru.yandex.practicum.telemetry.collector.model.DeviceType;
import ru.yandex.practicum.telemetry.collector.model.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.KafkaEventProducer;
import ru.yandex.practicum.telemetry.collector.utils.EnumMapper;

@Component
public class DeviceAddedHubEventHandler extends BaseHubEventHandler<DeviceAddedEvent> {

    public DeviceAddedHubEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_ADDED;
    }

    @Override
    protected GenericRecord mapToAvro(DeviceAddedEvent event) {
        return DeviceAddedEventAvro.newBuilder()
                .setId(event.getId())
                .setType(EnumMapper.map(event.getDeviceType(), DeviceType.class))
                .build();
    }
}
