package collector.mapper.proto;

import collector.mapper.proto.hub.HubProtoMapperService;
import collector.mapper.proto.sensor.SensorProtoMapperService;
import collector.model.hub.BaseDeviceEvent;
import collector.model.sensor.BaseSensorEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.collector.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.collector.SensorEventProto;

@Component
@RequiredArgsConstructor
public class ProtoMapper {

    private final SensorProtoMapperService sensorMapperService;
    private final HubProtoMapperService deviceMapperService;

    public SensorEventProto mapSensorEventToAvro(BaseSensorEvent event) {
        return sensorMapperService.mapToProto(event);
    }

    public HubEventProto mapHubEventToAvro(BaseDeviceEvent event) {
        return deviceMapperService.mapToProto(event);
    }


}