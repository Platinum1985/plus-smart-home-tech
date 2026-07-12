package collector.mapper.proto.sensor.mapper;

import collector.model.sensor.TemperatureSensorEvent;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.collector.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.collector.TemperatureSensorProto;

@Component("protoTemperatureSensorEventMapper")
public class TemperatureSensorEventMapper implements SensorProtoMapper<TemperatureSensorEvent> {
    @Override public Class<TemperatureSensorEvent> getEventType() { return TemperatureSensorEvent.class; }

    @Override
    public void mapPayload(TemperatureSensorEvent event, SensorEventProto.Builder builder) {
        TemperatureSensorProto payload = TemperatureSensorProto.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setTemperatureF(event.getTemperatureF())
                .build();
        builder.setTemperatureSensor(payload);
    }

    @Override
    public TemperatureSensorEvent mapFromProto(SensorEventProto proto) {
        TemperatureSensorProto p = proto.getTemperatureSensor();
        TemperatureSensorEvent event = new TemperatureSensorEvent();
        event.setTemperatureC(p.getTemperatureC());
        event.setTemperatureF(p.getTemperatureF());
        return event;
    }
}