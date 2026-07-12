package collector.mapper.avro.hub.mapper;

import collector.model.hub.DeviceRemovedEvent;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Component
public class DeviceRemovedEventMapper implements HubAvroMapper<DeviceRemovedEvent> {

    @Override
    public Class<DeviceRemovedEvent> getEventType() {
        return DeviceRemovedEvent.class;
    }

    public void mapPayload(DeviceRemovedEvent event, HubEventAvro avroEvent) {
        DeviceRemovedEventAvro data = new DeviceRemovedEventAvro();
        data.setId(event.getId());
        avroEvent.setPayload(data);
    }

}
