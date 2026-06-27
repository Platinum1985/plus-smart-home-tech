package ru.yandex.practicum.telemetry.collector.proto.sensor_converters;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.model.LightSensorEvent;
import ru.yandex.practicum.telemetry.collector.model.SensorEvent;

@Component
public class LightSensorConverter implements SensorEventConverter {
    @Override
    public SensorEvent convert(SensorEventProto request) {
        LightSensorEvent event = new LightSensorEvent();
        LightSensorProto proto = request.getLightSensor();

        // Специфичные поля
        event.setLinkQuality(proto.getLinkQuality());
        event.setLuminosity(proto.getLuminosity());

        // Общие поля
        copyCommonFields(request, event);

        return event;
    }

    @Override
    public SensorEventProto.PayloadCase getSupportedType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    protected void copyCommonFields(SensorEventProto proto, SensorEvent event) {
        event.setId(proto.getId());
        event.setHubId(proto.getHubId());
    }
}