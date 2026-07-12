package ru.yandex.practicum;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.collector.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.collector.ConditionTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.ScenarioCondition;

import java.util.Map;

@Log4j2
@Component
public class ConditionEvaluator {

    public boolean evaluate(ScenarioCondition scenarioCondition,
                            Map<String, SensorStateAvro> sensorStates) {
        SensorStateAvro state = sensorStates.get(scenarioCondition.getSensor().getId());
        if (state == null) return false;

        Condition conditionEntity = scenarioCondition.getCondition();
        ConditionOperationProto operation = ConditionOperationProto.valueOf(String.valueOf(conditionEntity.getOperation()));
        ConditionTypeProto type = ConditionTypeProto.valueOf(conditionEntity.getType());
        Object thresholdValue = scenarioCondition.getCondition().getValue();

        log.debug("Проверка условия: sensorId={}, type={}, operation={}, threshold={}",
                scenarioCondition.getSensor().getId(), type, operation, thresholdValue);

        Object actualValue = extractValue(state, type);
        if (actualValue == null) return false;

        return compare(actualValue, operation, thresholdValue);
    }

    private Object extractValue(SensorStateAvro state, ConditionTypeProto conditionType) {
        Object data = state.getData();

        return switch (conditionType) {
            case MOTION -> (data instanceof MotionSensorAvro ms) ? ms.getMotion() : null;
            case LUMINOSITY -> (data instanceof LightSensorAvro ls) ? ls.getLuminosity() : null;
            case SWITCH -> (data instanceof SwitchSensorAvro ss) ? ss.getState() : null;
            case TEMPERATURE -> extractTemperature(data);
            case CO2LEVEL -> (data instanceof ClimateSensorAvro cs) ? cs.getCo2Level() : null;
            case HUMIDITY -> (data instanceof ClimateSensorAvro cs) ? cs.getHumidity() : null;
            default -> null;
        };
    }

    private Object extractTemperature(Object data) {
        if (data instanceof ClimateSensorAvro climate) {
            return climate.getTemperatureC();
        }
        if (data instanceof TemperatureSensorAvro temperature) {
            return temperature.getTemperatureC();
        }
        return null;
    }

    private boolean compare(Object actual, ConditionOperationProto operation, Object threshold) {
        log.debug("Сравнение: actual={}, operation={}, threshold={}", actual, operation, threshold);

        return switch (actual) {
            case Boolean boolActual -> compareBoolean(boolActual, operation, threshold);
            case Integer intActual -> compareInteger(intActual, operation, threshold);
            default -> false;
        };
    }

    private boolean compareBoolean(boolean actual, ConditionOperationProto operation, Object threshold) {
        boolean thresholdValue = extractBooleanThreshold(threshold);
        return operation == ConditionOperationProto.EQUALS && actual == thresholdValue;
    }

    private boolean compareInteger(int actual, ConditionOperationProto operation, Object threshold) {
        Integer thresholdValue = extractIntegerThreshold(threshold);
        if (thresholdValue == null) return false;

        return switch (operation) {
            case EQUALS -> actual == thresholdValue;
            case GREATER_THAN -> actual > thresholdValue;
            case LOWER_THAN -> actual < thresholdValue;
            default -> false;
        };
    }

    private boolean extractBooleanThreshold(Object threshold) {
        return switch (threshold) {
            case Boolean b -> b;
            case Integer i -> i != 0;
            default -> false;
        };
    }

    private Integer extractIntegerThreshold(Object threshold) {
        return switch (threshold) {
            case Integer i -> i;
            case Long l -> l.intValue();
            default -> null;
        };
    }
}