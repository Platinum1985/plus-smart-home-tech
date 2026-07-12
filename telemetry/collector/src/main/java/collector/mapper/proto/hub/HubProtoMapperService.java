package collector.mapper.proto.hub;

import collector.mapper.proto.hub.mapper.HubProtoMapper;
import collector.model.hub.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.collector.HubEventProto;
import com.google.protobuf.Timestamp;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class HubProtoMapperService {
    private final HubProtoMapperFactory mapperFactory;

    public HubEventProto mapToProto(BaseDeviceEvent event) {
        HubEventProto.Builder builder = HubEventProto.newBuilder();

        // Общие поля
        builder.setHubId(event.getHubId());
        builder.setTimestamp(toProtoTimestamp(event.getTimestamp()));

        // Делегирование специфичному мапперу
        @SuppressWarnings("unchecked")
        HubProtoMapper<BaseDeviceEvent> mapper =
                (HubProtoMapper<BaseDeviceEvent>) mapperFactory.getMapper(event.getClass());

        mapper.mapPayload(event, builder);
        return builder.build();
    }

    public BaseDeviceEvent mapFromProto(HubEventProto proto) {
        HubEventProto.PayloadCase payloadCase = proto.getPayloadCase();
        Class<? extends BaseDeviceEvent> eventType = switch (payloadCase) {
            case DEVICE_ADDED -> DeviceAddedEvent.class;
            case DEVICE_REMOVED -> DeviceRemovedEvent.class;
            case SCENARIO_ADDED -> ScenarioAddedEvent.class;
            case SCENARIO_REMOVED -> ScenarioRemovedEvent.class;
            default -> throw new IllegalArgumentException("Unknown payload: " + payloadCase);
        };

        @SuppressWarnings("unchecked")
        HubProtoMapper<BaseDeviceEvent> mapper = (HubProtoMapper<BaseDeviceEvent>) mapperFactory.getMapper(eventType);

        BaseDeviceEvent event = mapper.mapFromProto(proto);
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