package collector.mapper.avro.sensor;

import collector.mapper.avro.sensor.mapper.SensorAvroMapper;
import collector.model.sensor.BaseSensorEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SensorMapperService {

    private final SensorAvroMapperFactory mapperFactory;

    public SensorEventAvro mapToAvro(BaseSensorEvent event) {
        SensorEventAvro avroEvent = createBaseAvroEvent(event);

        @SuppressWarnings("unchecked")
        SensorAvroMapper<BaseSensorEvent> mapper =
                (SensorAvroMapper<BaseSensorEvent>) mapperFactory.getMapper(event.getClass());

        mapper.mapPayload(event, avroEvent);
        return avroEvent;
    }

    private SensorEventAvro createBaseAvroEvent(BaseSensorEvent event) {
        SensorEventAvro avroEvent = new SensorEventAvro();
        avroEvent.setId(event.getId());
        avroEvent.setHubId(event.getHubId());
        avroEvent.setTimestamp(
                event.getTimestamp() != null
                        ? event.getTimestamp().toEpochMilli()
                        : Instant.now().toEpochMilli()
        );
        return avroEvent;
    }
}
