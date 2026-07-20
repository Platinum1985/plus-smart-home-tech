package collector.mapper.avro.sensor.mapper;

import collector.model.sensor.SwitchSensorEvent;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Component
public class SwitchSensorEventMapper implements SensorAvroMapper<SwitchSensorEvent> {

    @Override
    public Class<SwitchSensorEvent> getEventType() {
        return SwitchSensorEvent.class;
    }

    @Override
    public void mapPayload(SwitchSensorEvent event, SensorEventAvro avroEvent) {
        SwitchSensorAvro data = new SwitchSensorAvro();
        data.setState(event.getState());
        avroEvent.setPayload(data);
    }
}
