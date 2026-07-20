package collector.model.hub;

import collector.model.state.HubState;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import collector.model.state.DeviceType;

@Data
@EqualsAndHashCode(callSuper = true)
// Событие, сигнализирующее о добавлении нового устройства в систему
public class DeviceAddedEvent extends BaseDeviceEvent {
    @NotNull
    private String id;

    @NotNull
    private DeviceType deviceType;

    private HubState type = HubState.DEVICE_ADDED;


    @Override
    public String getType() {
        return type.toString();
    }

}