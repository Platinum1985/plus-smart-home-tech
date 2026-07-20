package collector.mapper.avro.sensor.mapper;

import collector.model.sensor.ClimateSensorEvent;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Component
public class ClimateSensorEventMapper implements SensorAvroMapper<ClimateSensorEvent> {

    @Override
    public Class<ClimateSensorEvent> getEventType() {
        return ClimateSensorEvent.class;
    }

    @Override
    public void mapPayload(ClimateSensorEvent event, SensorEventAvro avroEvent) {
        ClimateSensorAvro data = new ClimateSensorAvro();
        data.setTemperatureC(event.getTemperatureC());
        data.setHumidity(event.getHumidity());
        data.setCo2Level(event.getCo2Level());
        avroEvent.setPayload(data);
    }

}
