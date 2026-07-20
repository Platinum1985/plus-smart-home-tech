package collector.model;

import collector.model.state.ActionType;
import lombok.Data;

@Data
// Представляет действие, которое должно быть выполнено устройством.
public class DeviceAction {
    private String sensorId;        // Идентификатор датчика, связанного с действием
    private ActionType type;        // Тип действия при срабатывании условия активации сценария
    private Integer value;          // Необязательное значение, связанное с действием
}