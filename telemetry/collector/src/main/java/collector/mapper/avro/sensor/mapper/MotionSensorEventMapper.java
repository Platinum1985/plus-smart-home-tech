package collector.mapper.avro.sensor.mapper;

import collector.model.sensor.MotionSensorEvent;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Component
public class MotionSensorEventMapper implements SensorAvroMapper<MotionSensorEvent> {

    @Override
    public Class<MotionSensorEvent> getEventType() {
        return MotionSensorEvent.class;
    }

    @Override
    public void mapPayload(MotionSensorEvent event, SensorEventAvro avroEvent) {
        MotionSensorAvro data = new MotionSensorAvro();
        data.setLinkQuality(event.getLinkQuality());
        data.setMotion(event.getMotion());
        data.setVoltage(event.getVoltage());
        avroEvent.setPayload(data);
    }
}
