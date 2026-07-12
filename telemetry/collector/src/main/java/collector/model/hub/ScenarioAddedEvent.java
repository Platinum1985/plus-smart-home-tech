package collector.model.hub;

import collector.model.ScenarioCondition;
import collector.model.state.HubState;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import collector.model.DeviceAction;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
// Событие добавления сценария в систему. Содержит информацию о названии сценария, условиях и действиях.
public class ScenarioAddedEvent extends BaseDeviceEvent {

    @NotNull
    @Size(min = 3, max = 2147483646)
    private String name;                            // Название добавленного сценария

    private List<ScenarioCondition> conditions;     // Список условий, которые связаны со сценарием. Не может быть пустым

    private List<DeviceAction> actions;             // Список действий, которые должны быть выполнены в рамках сценария. Не может быть пустым

    private HubState type = HubState.SCENARIO_ADDED;

    @Override
    public String getType() {
        return type.toString();
    }
}