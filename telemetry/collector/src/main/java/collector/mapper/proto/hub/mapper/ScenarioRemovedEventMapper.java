package collector.mapper.proto.hub.mapper;

import collector.model.hub.ScenarioRemovedEvent;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.collector.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.collector.ScenarioRemovedEventProto;

@Component("protoScenarioRemovedEventMapper")
public class ScenarioRemovedEventMapper implements HubProtoMapper<ScenarioRemovedEvent> {
    @Override
    public Class<ScenarioRemovedEvent> getEventType() {
        return ScenarioRemovedEvent.class;
    }

    @Override
    public void mapPayload(ScenarioRemovedEvent event, HubEventProto.Builder builder) {
        ScenarioRemovedEventProto payload = ScenarioRemovedEventProto.newBuilder()
                .setName(event.getName())
                .build();
        builder.setScenarioRemoved(payload);
    }

    @Override
    public ScenarioRemovedEvent mapFromProto(HubEventProto proto) {
        ScenarioRemovedEventProto p = proto.getScenarioRemoved();
        ScenarioRemovedEvent event = new ScenarioRemovedEvent();
        event.setName(p.getName());
        return event;
    }
}