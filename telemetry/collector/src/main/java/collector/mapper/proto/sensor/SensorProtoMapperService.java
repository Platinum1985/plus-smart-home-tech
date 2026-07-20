package collector.mapper.proto.sensor;

import collector.mapper.proto.sensor.mapper.SensorProtoMapper;
import collector.model.sensor.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.google.protobuf.Timestamp;
import ru.yandex.practicum.grpc.telemetry.collector.SensorEventProto;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SensorProtoMapperService {
    private final SensorProtoMapperFactory mapperFactory;

    public SensorEventProto mapToProto(BaseSensorEvent event) {
        SensorEventProto.Builder builder = SensorEventProto.newBuilder();

        builder.setId(event.getId());
        builder.setHubId(event.getHubId());
        builder.setTimestamp(toProtoTimestamp(event.getTimestamp()));

        @SuppressWarnings("unchecked")
        SensorProtoMapper<BaseSensorEvent> mapper =
                (SensorProtoMapper<BaseSensorEvent>) mapperFactory.getMapper(event.getClass());

        mapper.mapPayload(event, builder);
        return builder.build();
    }

    public BaseSensorEvent mapFromProto(SensorEventProto proto) {
        SensorEventProto.PayloadCase payloadCase = proto.getPayloadCase();
        Class<? extends BaseSensorEvent> eventType = switch (payloadCase) {
            case MOTION_SENSOR -> MotionSensorEvent.class;
            case TEMPERATURE_SENSOR -> TemperatureSensorEvent.class;
            case LIGHT_SENSOR -> LightSensorEvent.class;
            case CLIMATE_SENSOR -> ClimateSensorEvent.class;
            case SWITCH_SENSOR -> SwitchSensorEvent.class;
            default -> throw new IllegalArgumentException("Unknown payload: " + payloadCase);
        };

        @SuppressWarnings("unchecked")
        SensorProtoMapper<BaseSensorEvent> mapper = (SensorProtoMapper<BaseSensorEvent>) mapperFactory.getMapper(eventType);

        BaseSensorEvent event = mapper.mapFromProto(proto);
        event.setId(proto.getId());
        event.setHubId(proto.getHubId());
        event.setTimestamp(toInstant(proto.getTimestamp()));
        return event;
    }

    private Timestamp toProtoTimestamp(Instant instant) {
        Instant time = instant != null ? instant : Instant.now();
        return Timestamp.newBuilder()
                .setSeconds(time.getEpochSecond())
                .setNanos(time.getNano())
                .build();
    }

    private Instant toInstant(Timestamp ts) {
        return ts != null ? Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos()) : Instant.now();
    }
}