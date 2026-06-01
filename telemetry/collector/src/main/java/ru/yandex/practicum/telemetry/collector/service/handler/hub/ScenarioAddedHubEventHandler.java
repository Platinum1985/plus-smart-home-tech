package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.KafkaClient;
import ru.yandex.practicum.telemetry.collector.model.*;
import ru.yandex.practicum.telemetry.collector.utils.EnumMapper;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ScenarioAddedHubEventHandler extends BaseHubEventHandler<ScenarioAddedEvent> {

    public ScenarioAddedHubEventHandler(KafkaClient producer) {
        super(producer);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }

    @Override
    protected HubEventAvro mapToAvro(ScenarioAddedEvent event) {
        List<ScenarioConditionAvro> avroConditions = event.getConditions() != null
                ? event.getConditions().stream()
                .map(condition -> ScenarioConditionAvro.newBuilder()
                        .setSensorId(condition.getSensorId())
                        .setType(EnumMapper.map(condition.getType(), ConditionTypeAvro.class))
                        .setOperation(EnumMapper.map(condition.getOperation(), ConditionOperationAvro.class))
                        .setValue(condition.getValue())
                        .build())
                .collect(Collectors.toList())
                : List.of(); // Пустой список, если conditions == null
        log.info("avroConditions ===== {}", avroConditions);
        List<DeviceActionAvro> avroActions = event.getActions() != null
                ? event.getActions().stream()
                .map(action -> DeviceActionAvro.newBuilder()
                        .setSensorId(action.getSensorId())
                        .setType(EnumMapper.map(action.getType(), ActionTypeAvro.class))
                        .setValue(action.getValue())
                        .build())
                .collect(Collectors.toList())
                : List.of();
        log.info("avroActions ===== {}", avroActions);
        // Создаём Avro-запись для payload
        ScenarioAddedEventAvro avroPayload = ScenarioAddedEventAvro.newBuilder()
                .setName(event.getName())
                .setConditions(avroConditions)
                .setActions(avroActions)
                .build();
        log.info("avroPayload ===== {}", avroPayload);
        // Возвращаем итоговый Avro-объект
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(avroPayload)
                .build();
    }
}

