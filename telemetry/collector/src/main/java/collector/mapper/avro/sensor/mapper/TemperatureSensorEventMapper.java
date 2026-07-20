package collector.mapper.avro.sensor.mapper;

import collector.model.sensor.TemperatureSensorEvent;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Component
public class TemperatureSensorEventMapper implements SensorAvroMapper<TemperatureSensorEvent> {

    @Override
    public Class<TemperatureSensorEvent> getEventType() {
        return TemperatureSensorEvent.class;
    }

    @Override
    public void mapPayload(TemperatureSensorEvent event, SensorEventAvro avroEvent) {
        TemperatureSensorAvro data = new TemperatureSensorAvro();
        data.setTemperatureC(event.getTemperatureC());
        data.setTemperatureF(event.getTemperatureF());
        avroEvent.setPayload(data);
    }
}
