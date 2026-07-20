package collector.model.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import collector.model.state.DeviceType;

@Data
@EqualsAndHashCode(callSuper = true)
// Событие датчика переключателя, содержащее информацию о текущем состоянии переключателя
public class SwitchSensorEvent extends BaseSensorEvent {
    @NotNull
    private Boolean state;          // Текущее состояние переключателя

    private DeviceType type = DeviceType.SWITCH_SENSOR;

    @Override
    public String getType() {
        return type.toString();
    }
}
