package collector.mapper.proto.hub.mapper;

import collector.model.hub.DeviceRemovedEvent;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.collector.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.collector.HubEventProto;

@Component("protoDeviceRemovedEventMapper")
public class DeviceRemovedEventMapper implements HubProtoMapper<DeviceRemovedEvent> {
    @Override
    public Class<DeviceRemovedEvent> getEventType() {
        return DeviceRemovedEvent.class;
    }

    @Override
    public void mapPayload(DeviceRemovedEvent event, HubEventProto.Builder builder) {
        DeviceRemovedEventProto payload = DeviceRemovedEventProto.newBuilder()
                .setId(event.getId())
                .build();
        builder.setDeviceRemoved(payload);
    }

    @Override
    public DeviceRemovedEvent mapFromProto(HubEventProto proto) {
        DeviceRemovedEventProto p = proto.getDeviceRemoved();
        DeviceRemovedEvent event = new DeviceRemovedEvent();
        event.setId(p.getId());
        return event;
    }
}