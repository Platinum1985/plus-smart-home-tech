package ru.yandex.practicum.telemetry.collector.proto.hub_converters;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.model.*;
import ru.yandex.practicum.telemetry.collector.model.HubEvent;
import ru.yandex.practicum.telemetry.collector.utils.EnumMapper;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScenarioAddedHubEventConverter implements HubEventConverter {

    @Override
    public HubEvent convert(HubEventProto proto) {
        ScenarioAddedEvent event = new ScenarioAddedEvent();
        ScenarioAddedEventProto scenarioProto = proto.getScenarioAdded();

        event.setHubId(proto.getHubId());
        event.setName(scenarioProto.getName());
        event.setConditions(convertConditions(scenarioProto.getConditionList()));
        event.setActions(convertActions(scenarioProto.getActionList()));
        com.google.protobuf.Timestamp protoTimestamp = proto.getTimestamp();
        Instant timestamp = Instant.ofEpochSecond(
                protoTimestamp.getSeconds(),
                protoTimestamp.getNanos()
        );
        event.setTimestamp(timestamp);

        return event;
    }

    @Override
    public HubEventProto.PayloadCase getSupportedType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    private List<ScenarioCondition> convertConditions(List<ScenarioConditionProto> protoConditions) {
        return protoConditions.stream()
                .map(this::convertCondition)
                .collect(Collectors.toList());
    }

    private ScenarioCondition convertCondition(ScenarioConditionProto proto) {
        ScenarioCondition condition = new ScenarioCondition();
        condition.setSensorId(proto.getSensorId());
        condition.setType(EnumMapper.map(proto.getType(), ConditionType.class)); //  !!!!!!!! Проверить!!!
        condition.setOperation(EnumMapper.map(proto.getOperation(), ConditionOperation.class)); // !!!!!

        if (proto.hasBoolValue()) {
            condition.setValue(proto.getBoolValue() ? 1 : 0); // преобразование boolean в Integer
        } else if (proto.hasIntValue()) {
            condition.setValue(proto.getIntValue());
        }

        return condition;
    }

    private List<DeviceAction> convertActions(List<DeviceActionProto> protoActions) {
        return protoActions.stream()
                .map(this::convertAction)
                .collect(Collectors.toList());
    }

    private DeviceAction convertAction(DeviceActionProto proto) {
        DeviceAction action = new DeviceAction();
        action.setSensorId(proto.getSensorId());
        action.setType(EnumMapper.map(proto.getType(), ActionType.class));

        if (proto.hasValue()) {
            action.setValue(proto.getValue()); // Optional<Integer>
        }

        return action;
    }
}
