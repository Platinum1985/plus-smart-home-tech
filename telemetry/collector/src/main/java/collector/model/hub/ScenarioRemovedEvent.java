package collector.model.hub;

import collector.model.state.HubState;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
// Событие удаления сценария из системы. Содержит информацию о названии удаленного сценария
public class ScenarioRemovedEvent extends BaseDeviceEvent {

    @NotNull
    @Size(min = 3, max = 2147483646)
    private String name;                            // Название добавленного сценария

    private HubState type = HubState.SCENARIO_REMOVED;

    @Override
    public String getType() {
        return type.toString();
    }
}