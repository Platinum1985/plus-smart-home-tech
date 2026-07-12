package collector.mapper.proto.sensor;

import collector.mapper.proto.sensor.mapper.SensorProtoMapper;
import collector.model.sensor.BaseSensorEvent;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SensorProtoMapperFactory {
    private final Map<Class<? extends BaseSensorEvent>, SensorProtoMapper<?>> mappers;

    public SensorProtoMapperFactory(List<SensorProtoMapper<?>> mapperList) {
        this.mappers = mapperList.stream()
                .collect(Collectors.toMap(SensorProtoMapper::getEventType, mapper -> mapper));
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseSensorEvent> SensorProtoMapper<T> getMapper(Class<T> eventType) {
        SensorProtoMapper<?> mapper = mappers.get(eventType);
        if (mapper == null) {
            throw new IllegalArgumentException("No proto mapper found for sensor event type: " + eventType.getSimpleName());
        }
        return (SensorProtoMapper<T>) mapper;
    }
}