package collector.mapper.proto.sensor.mapper;

import collector.model.sensor.ClimateSensorEvent;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.collector.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.collector.SensorEventProto;

@Component("protoClimateSensorEventMapper")
public class ClimateSensorEventMapper implements SensorProtoMapper<ClimateSensorEvent> {
    @Override public Class<ClimateSensorEvent> getEventType() { return ClimateSensorEvent.class; }

    @Override
    public void mapPayload(ClimateSensorEvent event, SensorEventProto.Builder builder) {
        ClimateSensorProto payload = ClimateSensorProto.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setHumidity(event.getHumidity())
                .setCo2Level(event.getCo2Level())
                .build();
        builder.setClimateSensor(payload);
    }

    @Override
    public ClimateSensorEvent mapFromProto(SensorEventProto proto) {
        ClimateSensorProto p = proto.getClimateSensor();
        ClimateSensorEvent event = new ClimateSensorEvent();
        event.setTemperatureC(p.getTemperatureC());
        event.setHumidity(p.getHumidity());
        event.setCo2Level(p.getCo2Level());
        return event;
    }
}