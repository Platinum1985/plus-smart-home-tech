package collector.mapper.proto.sensor.mapper;

import collector.model.sensor.SwitchSensorEvent;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.collector.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.collector.SwitchSensorProto;

@Component("protoSwitchSensorEventMapper")
public class SwitchSensorEventMapper implements SensorProtoMapper<SwitchSensorEvent> {
    @Override public Class<SwitchSensorEvent> getEventType() { return SwitchSensorEvent.class; }

    @Override
    public void mapPayload(SwitchSensorEvent event, SensorEventProto.Builder builder) {
        SwitchSensorProto payload = SwitchSensorProto.newBuilder()
                .setState(event.getState())
                .build();
        builder.setSwitchSensor(payload);
    }

    @Override
    public SwitchSensorEvent mapFromProto(SensorEventProto proto) {
        SwitchSensorProto p = proto.getSwitchSensor();
        SwitchSensorEvent event = new SwitchSensorEvent();
        event.setState(p.getState());
        return event;
    }
}