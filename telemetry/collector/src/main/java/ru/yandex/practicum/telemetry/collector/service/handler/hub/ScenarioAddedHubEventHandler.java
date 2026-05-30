package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.apache.avro.generic.GenericRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.KafkaClient;
import ru.yandex.practicum.telemetry.collector.model.*;
import ru.yandex.practicum.telemetry.collector.utils.EnumMapper;

import java.util.List;
import java.util.function.Function;

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
        // Преобразуем Java‑объект в Avro‑запись согласно схеме HubEventProtocol
        ScenarioAddedEventAvro avroPayload = ScenarioAddedEventAvro.newBuilder()  // Используем Avro‑класс
                .setName(event.getName())
                // Заполняем условия сценария с использованием EnumMapper
                .setConditions(List.of(event.getConditions().stream()
                        .map((Function<? super ScenarioCondition, ?>) condition -> ScenarioConditionAvro.newBuilder()
                                .setSensorId(condition.getSensorId())
                                // Безопасное преобразование enum через утилиту
                                .setType(EnumMapper.map(condition.getType(), ConditionTypeAvro.class))
                                .setOperation(EnumMapper.map(condition.getOperation(), ConditionOperationAvro.class))
                                // Сопоставляем thresholdValue с полем value в Avro
                                .setValue(condition.getThresholdValue())
                                .build()
                        )
                        .toArray(ScenarioConditionAvro[]::new))
                )
                // Заполняем действия сценария
                .setActions(List.of(event.getActions().stream()
                        .map((Function<? super DeviceAction, ?>) action -> DeviceActionAvro.newBuilder()
                                .setSensorId(action.getSensorId())
                                .setType(EnumMapper.map(action.getType(), ActionTypeAvro.class))  // Безопасное преобразование
                                .setValue(action.getValue())
                                .build()
                        )
                        .toArray(DeviceActionAvro[]::new))
                )
                .build();

        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(avroPayload)
                .build();
    }
}

