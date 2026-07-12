package collector.mapper.avro.sensor.mapper;

import collector.model.sensor.BaseSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public interface SensorAvroMapper<T extends BaseSensorEvent> {
    Class<T> getEventType();

    void mapPayload(T event, SensorEventAvro avroEvent);
}
