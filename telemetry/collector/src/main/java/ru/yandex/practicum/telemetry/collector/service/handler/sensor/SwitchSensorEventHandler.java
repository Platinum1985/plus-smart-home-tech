package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.apache.avro.generic.GenericRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.collector.model.SensorEventType;
import ru.yandex.practicum.telemetry.collector.model.SwitchSensorEvent;
import ru.yandex.practicum.telemetry.collector.model.KafkaEventProducer;

@Component
public class SwitchSensorEventHandler extends BaseSensorEventHandler<SwitchSensorEvent> {

    public SwitchSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }

    @Override
    protected GenericRecord mapToAvro(SwitchSensorEvent event) {
        SwitchSensorAvro avroPayload = SwitchSensorAvro.newBuilder()
                .setTemperatureC(event.getState())
                .build();

        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp().toEpochMilli())
                .setType(event.getType().name())
                .setPayload(avroPayload)
                .build();
    }
}
