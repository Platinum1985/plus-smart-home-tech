package ru.yandex.practicum.telemetry.collector.proto.sensor_converters;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.model.MotionSensorEvent;
import ru.yandex.practicum.telemetry.collector.model.SensorEvent;

import java.time.Instant;

@Component
public class MotionSensorConverter implements SensorEventConverter {
    @Override
    public SensorEvent convert(SensorEventProto request) {
        MotionSensorEvent event = new MotionSensorEvent();
        MotionSensorProto proto = request.getMotionSensor();

        // Специфичные поля
        event.setLinkQuality(proto.getLinkQuality());
        event.setMotion(proto.getMotion());
        event.setVoltage(proto.getVoltage());

        // Общие поля
        copyCommonFields(request, event);

        return event;
    }

    @Override
    public SensorEventProto.PayloadCase getSupportedType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR;
    }

    protected void copyCommonFields(SensorEventProto proto, SensorEvent event) {
        event.setId(proto.getId());
        event.setHubId(proto.getHubId());
        // Таймстемп уже инициализирован по умолчанию в SensorEvent
        com.google.protobuf.Timestamp protoTimestamp = proto.getTimestamp();
        Instant timestamp = Instant.ofEpochSecond(
                protoTimestamp.getSeconds(),
                protoTimestamp.getNanos()
        );
        event.setTimestamp(timestamp);
    }
}