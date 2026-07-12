package collector.model.hub;

import collector.model.state.HubState;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
// Событие, сигнализирующее о удалении устройства из системы
public class DeviceRemovedEvent extends BaseDeviceEvent {

    @NotNull
    private String id;

    private HubState type = HubState.DEVICE_REMOVED;

    @Override
    public String getType() {
        return type.toString();
    }
}