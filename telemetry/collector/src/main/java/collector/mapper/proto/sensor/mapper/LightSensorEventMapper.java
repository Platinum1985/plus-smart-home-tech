package collector.mapper.proto.sensor.mapper;

import collector.model.sensor.LightSensorEvent;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.collector.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.collector.SensorEventProto;

@Component("protoLightSensorEventMapper")
public class LightSensorEventMapper implements SensorProtoMapper<LightSensorEvent> {
    @Override public Class<LightSensorEvent> getEventType() { return LightSensorEvent.class; }

    @Override
    public void mapPayload(LightSensorEvent event, SensorEventProto.Builder builder) {
        LightSensorProto payload = LightSensorProto.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setLuminosity(event.getLuminosity())
                .build();
        builder.setLightSensor(payload);
    }

    @Override
    public LightSensorEvent mapFromProto(SensorEventProto proto) {
        LightSensorProto p = proto.getLightSensor();
        LightSensorEvent event = new LightSensorEvent();
        event.setLinkQuality(p.getLinkQuality());
        event.setLuminosity(p.getLuminosity());
        return event;
    }
}