package ru.yandex.practicum.telemetry.collector.proto.sensor_converters;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.telemetry.collector.model.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.SwitchSensorEvent;

import java.time.Instant;

@Component
public class SwitchSensorConverter implements SensorEventConverter {
    @Override
    public SensorEvent convert(SensorEventProto request) {
        SwitchSensorEvent event = new SwitchSensorEvent();
        SwitchSensorProto proto = request.getSwitchSensor();

        // Специфичные поля
        event.setState(proto.getState());

        // Общие поля
        copyCommonFields(request, event);

        return event;
    }

    @Override
    public SensorEventProto.PayloadCase getSupportedType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR;
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
