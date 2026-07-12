package collector.model.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import collector.model.state.DeviceType;

@Data
@EqualsAndHashCode(callSuper = true)
// Событие климатического датчика, содержащее информацию о температуре, влажности и уровне CO2
public class ClimateSensorEvent extends BaseSensorEvent {

    @NotNull
    private Integer temperatureC;       // Уровень температуры по шкале Цельсия.

    @NotNull
    private Integer humidity;           // Влажность.

    @NotNull
    private Integer co2Level;           // Уровень CO2.

    private DeviceType type = DeviceType.CLIMATE_SENSOR;

    @Override
    public String getType() {
        return type.toString();
    }
}