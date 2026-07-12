package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.ScenarioAction;

import java.util.List;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class SnapshotEvaluationService {

    private final ScenarioService scenarioService;
    private final ActionCollector actionCollector;
    private final ActionSender actionSender;

    @Transactional
    public void evaluateSnapshot(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        Map<String, SensorStateAvro> sensorStates = snapshot.getSensorsState();
        log.info("Обработка снапшота для хаба: {}, датчиков: {}", hubId, sensorStates.size());
        List<Scenario> scenarios = scenarioService.getScenariosByHubId(hubId);
        log.info("Найдено сценариев для хаба {}: {}", hubId, scenarios.size());

        for (Scenario scenario : scenarios) {
            List<ScenarioAction> actions = actionCollector.collectActions(scenario, sensorStates);
            if (!actions.isEmpty()) {
                log.info("Сценарий {} активирован, отправка {} действий", scenario.getName(), actions.size());

                for (ScenarioAction action : actions) {
                    actionSender.sendAction(hubId, scenario.getName(), action);
                }
            } else {
                log.debug("Сценарий {} не активирован", scenario.getName());
            }
        }
    }
}