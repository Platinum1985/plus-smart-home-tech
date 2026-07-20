package collector.model;

import lombok.Data;
import collector.model.state.ConditionType;
import collector.model.state.ConditionOperation;

@Data
// Условие сценария, которое содержит информацию о датчике, типе условия, операции и значении
public class ScenarioCondition {
    private String sensorId;                // Идентификатор датчика, связанного с условием
    private ConditionType type;             // Тип условия
    private ConditionOperation operation;   // Операции
    private Integer value;                  // Значение, используемое в условии
}