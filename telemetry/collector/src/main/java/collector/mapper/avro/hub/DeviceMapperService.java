package collector.mapper.avro.hub;

import collector.mapper.avro.hub.mapper.HubAvroMapper;
import collector.model.hub.BaseDeviceEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class DeviceMapperService {

    private final DeviceAvroMapperFactory mapperFactory;

    public HubEventAvro mapToAvro(BaseDeviceEvent event) {
        HubEventAvro avroEvent = createBaseAvroEvent(event);

        @SuppressWarnings("unchecked")
        HubAvroMapper<BaseDeviceEvent> mapper =
                (HubAvroMapper<BaseDeviceEvent>) mapperFactory.getMapper(event.getClass());

        mapper.mapPayload(event, avroEvent);
        return avroEvent;
    }

    private HubEventAvro createBaseAvroEvent(BaseDeviceEvent event) {
        HubEventAvro avroEvent = new HubEventAvro();

        avroEvent.setHubId(event.getHubId());

        avroEvent.setTimestamp(
                event.getTimestamp() != null
                        ? event.getTimestamp().toEpochMilli()
                        : Instant.now().toEpochMilli()
        );

        return avroEvent;
    }
}
