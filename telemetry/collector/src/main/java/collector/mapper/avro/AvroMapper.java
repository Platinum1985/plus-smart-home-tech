package collector.mapper.avro;

import collector.mapper.avro.hub.DeviceMapperService;
import collector.mapper.avro.sensor.SensorMapperService;
import collector.model.hub.BaseDeviceEvent;

import collector.model.sensor.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Component
@RequiredArgsConstructor
public class AvroMapper {

    private final SensorMapperService sensorMapperService;
    private final DeviceMapperService deviceMapperService;

    public SensorEventAvro mapSensorEventToAvro(BaseSensorEvent event) {
        return sensorMapperService.mapToAvro(event);
    }

    public HubEventAvro mapHubEventToAvro(BaseDeviceEvent event) {
        return deviceMapperService.mapToAvro(event);
    }


}