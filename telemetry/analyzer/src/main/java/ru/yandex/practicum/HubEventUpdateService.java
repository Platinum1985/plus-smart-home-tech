package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.collector.ConditionOperationProto;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.model.ScenarioCondition.ScenarioConditionId;
import ru.yandex.practicum.repository.*;
import ru.yandex.practicum.model.ScenarioAction.ScenarioActionId;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class HubEventUpdateService {

    private final SensorRepository sensorRep;
    private final ScenarioRepository scenarioRep;
    private final ConditionRepository conditionRep;
    private final ActionRepository actionRep;
    private final ScenarioConditionRepository scenarioConditionRep;
    private final ScenarioActionRepository scenarioActionRep;

    public void processHubEvent(HubEventAvro event) {
        Object payload = event.getPayload();
        switch (payload) {
            case DeviceAddedEventAvro added -> addDevice(event.getHubId(), added);
            case DeviceRemovedEventAvro removed -> removeDevice(removed);
            case ScenarioAddedEventAvro added -> addScenario(event.getHubId(), added);
            case ScenarioRemovedEventAvro removed -> removeScenario(event.getHubId(), removed);
            default -> log.debug("Тип события не найден: {}", payload.getClass().getSimpleName());
        }
    }

    private void addDevice(String hubId, DeviceAddedEventAvro added) {
        if (sensorRep.existsById(added.getId())) {
            log.debug("Устройство уже существует: {}", added.getId());
            return;
        }

        sensorRep.save(
                Sensor.builder()
                        .id(added.getId())
                        .hubId(hubId)
                        .build()
        );

        log.info("Устройство добавлено: {}", added.getId());
    }

    private void removeDevice(DeviceRemovedEventAvro removed) {
        sensorRep.deleteById(removed.getId());
        log.info("Устройство удалено: {}", removed.getId());
    }

    // Добавление сценария
    private void addScenario(String hubId, ScenarioAddedEventAvro added) {
        if (scenarioRep.findByHubIdAndName(hubId, added.getName()).isPresent()) {
            log.info("Сценарий уже существует: {}", added.getName());
            return;
        }

        // Создаём сценарий
        Scenario scenario = scenarioRep
                .save(Scenario.builder()
                        .hubId(hubId)
                        .name(added.getName())
                        .build()
                );

        // Сохраняем условия
        List<ScenarioCondition> conditionsToSave = new ArrayList<>();
        for (ScenarioConditionAvro condAvro : added.getConditions()) {
            Condition condition = conditionRep.save(
                    Condition.builder()
                            .type(condAvro.getType().name())
                            .operation(ConditionOperationProto.valueOf(condAvro.getOperation().name()))
                            .value(getValue(condAvro.getValue()))
                            .build()
            );

            Sensor sensor = getSensor(condAvro.getSensorId());

            conditionsToSave.add(
                    ScenarioCondition.builder()
                            .id(ScenarioConditionId.builder()
                                    .scenarioId(scenario.getId())
                                    .sensorId(sensor.getId())
                                    .conditionId(condition.getId())
                                    .build())
                            .condition(condition)
                            .scenario(scenario)
                            .sensor(sensor)
                            .build()
            );
        }
        scenarioConditionRep.saveAll(conditionsToSave);

        // Сохраняем действия
        List<ScenarioAction> actionsToSave = new ArrayList<>();
        for (DeviceActionAvro actionAvro : added.getActions()) {
            Action action = actionRep.save(
                    Action.builder()
                            .sensorId(actionAvro.getSensorId())
                            .type(actionAvro.getType().name())
                            .value(actionAvro.getValue())
                            .build()
            );

            Sensor sensor = getSensor(actionAvro.getSensorId());

            actionsToSave.add(
                    ScenarioAction.builder()
                            .id(ScenarioActionId.builder()
                                    .scenarioId(scenario.getId())
                                    .sensorId(sensor.getId())
                                    .actionId(action.getId())
                                    .build())
                            .scenario(scenario)
                            .sensor(sensor)
                            .action(action)
                            .build()
            );
        }
        scenarioActionRep.saveAll(actionsToSave);  // ← один запрос
        log.info("Сценарий добавлен: {}", added.getName());
    }

    private void removeScenario(String hubId, ScenarioRemovedEventAvro removed) {
        scenarioRep.findByHubIdAndName(hubId, removed.getName())
                .ifPresent(scenario -> {
                    scenarioRep.delete(scenario);
                    log.info("Сценарий удалён: {}", removed.getName());
                });
    }

    // Получение типизированного значения
    private Integer getValue(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        }
        return null;
    }

    private Sensor getSensor(String id) {
        return sensorRep.findById(id)
                .orElseThrow(() -> new RuntimeException("Сенсор не найден: " + id));
    }
}