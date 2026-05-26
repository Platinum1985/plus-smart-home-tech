package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.apache.avro.generic.GenericRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.collector.model.LightSensorEvent;
import ru.yandex.practicum.telemetry.collector.model.SensorEventType;
import ru.yandex.practicum.telemetry.collector.model.KafkaEventProducer;

@Component
public class LightSensorEventHandler extends BaseSensorEventHandler<LightSensorEvent> {

    public LightSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }

    @Override
    protected GenericRecord mapToAvro(LightSensorEvent event) {
        // Создаём запись Avro для полезной нагрузки
        LightSensorAvro avroPayload = LightSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setLuminosity(event.getLuminosity())
                .build();

        // Заполняем основную запись SensorEventAvro
        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp().toEpochMilli())
                .setType(event.getType().name())
                .setPayload(avroPayload)
                .build();
    }
}
