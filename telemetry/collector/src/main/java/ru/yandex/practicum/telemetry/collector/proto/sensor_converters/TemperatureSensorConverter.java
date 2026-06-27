package ru.yandex.practicum.telemetry.collector.proto.sensor_converters;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.telemetry.collector.model.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.TemperatureSensorEvent;

@Component
public class TemperatureSensorConverter implements SensorEventConverter {
    @Override
    public SensorEvent convert(SensorEventProto request) {
        TemperatureSensorEvent event = new TemperatureSensorEvent();
        TemperatureSensorProto proto = request.getTemperatureSensor();

        // Специфичные поля
        event.setTemperatureC(proto.getTemperatureC());
        event.setTemperatureF(proto.getTemperatureF());

        // Общие поля
        copyCommonFields(request, event);

        return event;
    }

    @Override
    public SensorEventProto.PayloadCase getSupportedType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR;
    }

    protected void copyCommonFields(SensorEventProto proto, SensorEvent event) {
        event.setId(proto.getId());
        event.setHubId(proto.getHubId());
    }
}