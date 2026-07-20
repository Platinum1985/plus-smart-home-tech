package collector.mapper.proto.hub.mapper;

import collector.model.hub.DeviceAddedEvent;
import collector.model.state.DeviceType;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.collector.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.collector.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.collector.HubEventProto;

@Component("protoDeviceAddedEventMapper")
public class DeviceAddedEventMapper implements HubProtoMapper<DeviceAddedEvent> {
    @Override
    public Class<DeviceAddedEvent> getEventType() {
        return DeviceAddedEvent.class;
    }

    @Override
    public void mapPayload(DeviceAddedEvent event, HubEventProto.Builder builder) {
        DeviceAddedEventProto payload = DeviceAddedEventProto.newBuilder()
                .setId(event.getId())
                .setType(mapDeviceType(String.valueOf(event.getDeviceType())))
                .build();

        // Заполняем поле oneof
        builder.setDeviceAdded(payload);
    }

    @Override
    public DeviceAddedEvent mapFromProto(HubEventProto proto) {
        DeviceAddedEventProto p = proto.getDeviceAdded();
        DeviceAddedEvent event = new DeviceAddedEvent();
        event.setId(p.getId());
        event.setDeviceType(DeviceType.valueOf(p.getType().name()));
        return event;
    }

    private DeviceTypeProto mapDeviceType(String type) {
        // Приведите к вашему enum/string в модели
        return DeviceTypeProto.valueOf(type.toUpperCase());
    }
}