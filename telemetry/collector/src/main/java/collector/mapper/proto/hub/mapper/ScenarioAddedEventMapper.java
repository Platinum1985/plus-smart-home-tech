package collector.mapper.proto.hub.mapper;

import collector.model.DeviceAction;
import collector.model.ScenarioCondition;
import collector.model.hub.ScenarioAddedEvent;
import collector.model.state.ActionType;
import collector.model.state.ConditionOperation;
import collector.model.state.ConditionType;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.collector.*;

import java.util.stream.Collectors;

@Component("protoScenarioAddedEventMapper")
public class ScenarioAddedEventMapper implements HubProtoMapper<ScenarioAddedEvent> {
    @Override
    public Class<ScenarioAddedEvent> getEventType() {
        return ScenarioAddedEvent.class;
    }

    @Override
    public void mapPayload(ScenarioAddedEvent event, HubEventProto.Builder builder) {
        ScenarioAddedEventProto.Builder payloadBuilder = ScenarioAddedEventProto.newBuilder()
                .setName(event.getName());

        event.getConditions().stream()
                .map(this::mapConditionToProto)
                .forEach(payloadBuilder::addCondition);

        event.getActions().stream()
                .map(this::mapActionToProto)
                .forEach(payloadBuilder::addAction);

        builder.setScenarioAdded(payloadBuilder.build());
    }

    @Override
    public ScenarioAddedEvent mapFromProto(HubEventProto proto) {
        ScenarioAddedEventProto p = proto.getScenarioAdded();
        ScenarioAddedEvent event = new ScenarioAddedEvent();
        event.setName(p.getName());
        event.setConditions(p.getConditionList().stream().map(this::mapConditionFromProto).collect(Collectors.toList()));
        event.setActions(p.getActionList().stream().map(this::mapActionFromProto).collect(Collectors.toList()));
        return event;
    }

    private ScenarioConditionProto mapConditionToProto(ScenarioCondition c) {
        ScenarioConditionProto.Builder b = ScenarioConditionProto.newBuilder()
                .setSensorId(c.getSensorId())
                .setType(ConditionTypeProto.valueOf(c.getType().name()))
                .setOperation(ConditionOperationProto.valueOf(c.getOperation().name()));
        if (c.getValue() != null) b.setIntValue(c.getValue());
        return b.build();
    }

    private ScenarioCondition mapConditionFromProto(ScenarioConditionProto p) {
        ScenarioCondition c = new ScenarioCondition();
        c.setSensorId(p.getSensorId());
        c.setType(ConditionType.valueOf(p.getType().name()));
        c.setOperation(ConditionOperation.valueOf(p.getOperation().name()));
        if (p.hasIntValue()) c.setValue(p.getIntValue());
        else if (p.hasBoolValue()) c.setValue(p.getBoolValue() ? 1 : 0);
        return c;
    }

    private DeviceActionProto mapActionToProto(DeviceAction a) {
        DeviceActionProto.Builder b = DeviceActionProto.newBuilder()
                .setSensorId(a.getSensorId())
                .setType(ActionTypeProto.valueOf(a.getType().name()));
        if (a.getValue() != null) b.setValue(a.getValue());
        return b.build();
    }

    private DeviceAction mapActionFromProto(DeviceActionProto p) {
        DeviceAction a = new DeviceAction();
        a.setSensorId(p.getSensorId());
        a.setType(ActionType.valueOf(p.getType().name()));
        if (p.hasValue()) a.setValue(p.getValue());
        return a;
    }
}