package ru.yandex.practicum.telemetry.collector.proto.hub_converters;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.telemetry.collector.model.DeviceRemovedEvent;
import ru.yandex.practicum.telemetry.collector.model.HubEvent;

import java.time.Instant;

@Component
public class DeviceRemovedHubEventConverter implements HubEventConverter {

    @Override
    public HubEvent convert(HubEventProto proto) {
        DeviceRemovedEvent event = new DeviceRemovedEvent();
        DeviceRemovedEventProto deviceProto = proto.getDeviceRemoved();

        event.setId(deviceProto.getId());
        event.setHubId(proto.getHubId());

        // Преобразуем Timestamp (Protobuf) в Instant java
        com.google.protobuf.Timestamp protoTimestamp = proto.getTimestamp();
        Instant timestamp = Instant.ofEpochSecond(
                protoTimestamp.getSeconds(),
                protoTimestamp.getNanos()
        );
        event.setTimestamp(timestamp);

        return event;
    }

    @Override
    public HubEventProto.PayloadCase getSupportedType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }
}
