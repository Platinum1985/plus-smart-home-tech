package collector.mapper.avro.hub.mapper;

import collector.model.hub.DeviceAddedEvent;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Component
public class DeviceAddedEventMapper implements HubAvroMapper<DeviceAddedEvent> {

    @Override
    public Class<DeviceAddedEvent> getEventType() {
        return DeviceAddedEvent.class;
    }

    @Override
    public void mapPayload(DeviceAddedEvent event, HubEventAvro avroEvent) {
        DeviceAddedEventAvro data = new DeviceAddedEventAvro();
        data.setId(event.getId());
        data.setType(DeviceTypeAvro.valueOf(event.getDeviceType().toString()));
        avroEvent.setPayload(data);
    }

}
