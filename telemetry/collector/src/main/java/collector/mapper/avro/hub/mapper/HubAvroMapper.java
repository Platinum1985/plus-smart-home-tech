package collector.mapper.avro.hub.mapper;

import collector.model.hub.BaseDeviceEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubAvroMapper<T extends BaseDeviceEvent> {
    Class<T> getEventType();

    void mapPayload(T event, HubEventAvro avroEvent);
}
