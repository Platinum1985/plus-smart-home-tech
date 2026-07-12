package collector.model.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import collector.model.state.DeviceType;

@Data
@EqualsAndHashCode(callSuper = true)
// Событие датчика движения
public class MotionSensorEvent extends BaseSensorEvent {

    @NotNull
    private Integer linkQuality;    // Качество связи

    @NotNull
    private Boolean motion;         // Наличие/отсутствие движения

    @NotNull
    private Integer voltage;        // Напряжение

    private DeviceType type = DeviceType.MOTION_SENSOR;

    @Override
    public String getType() {
        return type.toString();
    }
}
