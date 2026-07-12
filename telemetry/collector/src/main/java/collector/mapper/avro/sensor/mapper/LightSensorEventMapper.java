package collector.mapper.avro.sensor.mapper;

import collector.model.sensor.LightSensorEvent;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Component
public class LightSensorEventMapper implements SensorAvroMapper<LightSensorEvent> {

    @Override
    public Class<LightSensorEvent> getEventType() {
        return LightSensorEvent.class;
    }

    @Override
    public void mapPayload(LightSensorEvent event, SensorEventAvro avroEvent) {
        LightSensorAvro data = new LightSensorAvro();
        data.setLinkQuality(event.getLinkQuality());
        data.setLuminosity(event.getLuminosity());
        avroEvent.setPayload(data);
    }

}
