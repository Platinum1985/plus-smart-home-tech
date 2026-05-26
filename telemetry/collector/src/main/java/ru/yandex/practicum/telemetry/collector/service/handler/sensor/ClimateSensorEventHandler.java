package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.apache.avro.generic.GenericRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.collector.model.ClimateSensorEvent;
import ru.yandex.practicum.telemetry.collector.model.SensorEventType;
import ru.yandex.practicum.telemetry.collector.model.KafkaEventProducer;

@Component
public class ClimateSensorEventHandler extends BaseSensorEventHandler<ClimateSensorEvent> {
    public ClimateSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }

    @Override
    protected GenericRecord mapToAvro(ClimateSensorEvent event) {
        // Создаём запись Avro для полезной нагрузки
        ClimateSensorAvro avroPayload = ClimateSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setHumidity(event.getHumidity())
                .setCo2Level(event.getCo2Level())
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
