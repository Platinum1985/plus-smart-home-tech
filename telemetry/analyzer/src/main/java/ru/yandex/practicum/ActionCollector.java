package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.ScenarioAction;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class ActionCollector {

    private final ConditionEvaluator evaluator;

    public List<ScenarioAction> collectActions(Scenario scenario, Map<String, SensorStateAvro> sensorStates) {
        log.info("Обработка сценария: {}, количество условий: {}", scenario.getName(), scenario.getConditions().size());
        boolean allConditionsMet = scenario.getConditions().stream()
                .peek(sc -> log.debug("Условие для сенсора {}: результат={}", sc.getSensor().getId(), evaluator.evaluate(sc, sensorStates)))
                .allMatch(sc -> evaluator.evaluate(sc, sensorStates));
        if (allConditionsMet) {
            log.info("Сценарий {} активирован, количество действий: {}", scenario.getName(), scenario.getActions().size());
            return new ArrayList<>(scenario.getActions());
        } else {
            log.info("Сценарий {} НЕ активирован", scenario.getName());
            return List.of();
        }
    }
}
