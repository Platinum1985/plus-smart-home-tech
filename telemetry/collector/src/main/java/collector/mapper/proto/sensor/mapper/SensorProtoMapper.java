package collector.mapper.proto.sensor.mapper;

import collector.model.sensor.BaseSensorEvent;
import ru.yandex.practicum.grpc.telemetry.collector.SensorEventProto;

public interface SensorProtoMapper<T extends BaseSensorEvent> {
    Class<T> getEventType();
    void mapPayload(T event, SensorEventProto.Builder builder);
    T mapFromProto(SensorEventProto proto);
}