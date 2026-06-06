package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.KafkaClient;
import ru.yandex.practicum.telemetry.collector.model.ClimateSensorEvent;
import ru.yandex.practicum.telemetry.collector.model.SensorEventType;

@Component
public class ClimateSensorEventHandler extends BaseSensorEventHandler<ClimateSensorEvent> {
    public ClimateSensorEventHandler(KafkaClient producer) {
        super(producer);
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }

    @Override
    protected SensorEventAvro mapToAvro(ClimateSensorEvent event) {
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
                .setTimestamp(event.getTimestamp())
                .setPayload(avroPayload)
                .build();
    }
}
