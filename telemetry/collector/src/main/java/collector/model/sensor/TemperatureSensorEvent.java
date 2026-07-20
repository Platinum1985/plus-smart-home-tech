package collector.model.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import collector.model.state.DeviceType;

@Data
@EqualsAndHashCode(callSuper = true)
// Событие датчика температуры, содержащее информацию о температуре в градусах Цельсия и Фаренгейта
public class TemperatureSensorEvent extends BaseSensorEvent {
    @NotNull
    private Integer temperatureC;   // Температура в градусах Цельсия
    @NotNull
    private Integer temperatureF;   // Температура в градусах Фаренгейта

    private DeviceType type = DeviceType.TEMPERATURE_SENSOR;

    @Override
    public String getType() {
        return type.toString();
    }
}