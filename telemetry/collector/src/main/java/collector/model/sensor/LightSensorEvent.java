package collector.model.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import collector.model.state.DeviceType;

@Data
@EqualsAndHashCode(callSuper = true)
// Событие датчика освещенности, содержащее информацию о качестве связи и уровне освещенности
public class LightSensorEvent extends BaseSensorEvent {

    @NotNull
    private Integer linkQuality;    // Качество связи

    @NotNull
    private Integer luminosity;     // Уровень освещенности

    private DeviceType type = DeviceType.LIGHT_SENSOR;

    @Override
    public String getType() {
        return type.toString();
    }
}
