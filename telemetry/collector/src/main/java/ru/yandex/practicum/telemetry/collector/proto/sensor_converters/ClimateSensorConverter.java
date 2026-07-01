package ru.yandex.practicum.telemetry.collector.proto.sensor_converters;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.model.ClimateSensorEvent;
import ru.yandex.practicum.telemetry.collector.model.SensorEvent;

import java.time.Instant;

@Component
public class ClimateSensorConverter implements SensorEventConverter {
    @Override
    public SensorEvent convert(SensorEventProto request) {
        ClimateSensorEvent event = new ClimateSensorEvent();
        ClimateSensorProto proto = request.getClimateSensor();

        // Специфичные поля
        event.setTemperatureC(proto.getTemperatureC());
        event.setHumidity(proto.getHumidity());
        event.setCo2Level(proto.getCo2Level());

        // Общие поля
        copyCommonFields(request, event);

        return event;
    }

    @Override
    public SensorEventProto.PayloadCase getSupportedType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR;
    }

    protected void copyCommonFields(SensorEventProto proto, SensorEvent event) {
        event.setId(proto.getId());
        event.setHubId(proto.getHubId());
        com.google.protobuf.Timestamp protoTimestamp = proto.getTimestamp();
        Instant timestamp = Instant.ofEpochSecond(
                protoTimestamp.getSeconds(),
                protoTimestamp.getNanos()
        );
        event.setTimestamp(timestamp);
    }
}
