package ru.yandex.practicum.telemetry.collector.proto.hub_converters;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.telemetry.collector.model.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.ScenarioRemovedEvent;

@Component
public class ScenarioRemovedHubEventConverter implements HubEventConverter {

    @Override
    public HubEvent convert(HubEventProto proto) {
        ScenarioRemovedEvent event = new ScenarioRemovedEvent();
        ScenarioRemovedEventProto scenarioProto = proto.getScenarioRemoved();

        event.setName(scenarioProto.getName());

        // DeviceType может быть не установлен — зависит от требований
        // Если нужно, добавьте логику заполнения DeviceType

        return event;
    }

    @Override
    public HubEventProto.PayloadCase getSupportedType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }
}
