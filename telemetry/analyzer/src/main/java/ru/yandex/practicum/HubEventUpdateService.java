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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


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

        // Шаг 1: собираем ID сенсоров из условий и действий
        Set<String> allSensorIds = new HashSet<>(added.getConditions().stream()
                .map(ScenarioConditionAvro::getSensorId)
                .collect(Collectors.toList()));
        allSensorIds.addAll(added.getActions().stream()
                .map(DeviceActionAvro::getSensorId)
                .collect(Collectors.toList()));

        // Шаг 2: выполняем один запрос к БД для получения всех сенсоров
        List<Sensor> sensors = sensorRep.findByIdIn(allSensorIds);

        // Шаг 3: создаём маппинг «ID сенсора → сенсор» для быстрого доступа
        Map<String, Sensor> sensorMap = sensors.stream()
                .collect(Collectors.toMap(Sensor::getId, Function.identity()));

        // Шаг 4: проверяем, что все сенсоры найдены
        if (sensorMap.size() < allSensorIds.size()) {
            List<String> missingIds = allSensorIds.stream()
                    .filter(id -> !sensorMap.containsKey(id))
                    .collect(Collectors.toList());
            throw new RuntimeException("Сенсоры не найдены: " + missingIds);
        }

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

            Sensor sensor = sensorMap.get(condAvro.getSensorId()); // берём сенсор из маппинга

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

            Sensor sensor = sensorMap.get(actionAvro.getSensorId()); // берём сенсор из маппинга

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
}
