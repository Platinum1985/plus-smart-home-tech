package ru.yandex.practicum.telemetry.collector.proto.hub_converters;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.telemetry.collector.model.DeviceAddedEvent;
import ru.yandex.practicum.telemetry.collector.model.DeviceType;
import ru.yandex.practicum.telemetry.collector.model.HubEvent;

import java.time.Instant;

@Component
public class DeviceAddedHubEventConverter implements HubEventConverter {

    @Override
    public HubEvent convert(HubEventProto proto) {
        DeviceAddedEvent event = new DeviceAddedEvent();
        DeviceAddedEventProto deviceProto = proto.getDeviceAdded();

        event.setHubId(proto.getHubId());

        // Преобразуем Timestamp (Protobuf) в Instant java
        com.google.protobuf.Timestamp protoTimestamp = proto.getTimestamp();
        Instant timestamp = Instant.ofEpochSecond(
                protoTimestamp.getSeconds(),
                protoTimestamp.getNanos()
        );
        event.setTimestamp(timestamp);

        event.setId(deviceProto.getId());
        event.setDeviceType(convertDeviceType(deviceProto.getType()));

        return event;
    }

    @Override
    public HubEventProto.PayloadCase getSupportedType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    private DeviceType convertDeviceType(DeviceTypeProto protoType) {
        return switch (protoType) {
            case MOTION_SENSOR -> DeviceType.MOTION_SENSOR;
            case TEMPERATURE_SENSOR -> DeviceType.TEMPERATURE_SENSOR;
            case LIGHT_SENSOR -> DeviceType.LIGHT_SENSOR;
            case CLIMATE_SENSOR -> DeviceType.CLIMATE_SENSOR;
            case SWITCH_SENSOR -> DeviceType.SWITCH_SENSOR;
            default -> throw new IllegalArgumentException("Unknown device type: " + protoType);
        };
    }
}