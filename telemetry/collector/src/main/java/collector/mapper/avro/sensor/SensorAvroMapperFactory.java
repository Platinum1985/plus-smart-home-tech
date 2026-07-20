package collector.mapper.avro.sensor;

import collector.mapper.avro.sensor.mapper.SensorAvroMapper;
import collector.model.sensor.BaseSensorEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SensorAvroMapperFactory {

    private final Map<Class<? extends BaseSensorEvent>, SensorAvroMapper<?>> mappers;

    public SensorAvroMapperFactory(List<SensorAvroMapper<?>> mapperList) {
        this.mappers = mapperList.stream()
                .collect(Collectors.toMap(
                        SensorAvroMapper::getEventType,
                        mapper -> mapper
                ));
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseSensorEvent> SensorAvroMapper<T> getMapper(Class<T> eventType) {
        SensorAvroMapper<?> mapper = mappers.get(eventType);
        if (mapper == null) {
            throw new IllegalArgumentException(
                    "No mapper found for event type: " + eventType.getSimpleName()
            );
        }
        return (SensorAvroMapper<T>) mapper;
    }

    public boolean hasMapper(Class<? extends BaseSensorEvent> eventType) {
        return mappers.containsKey(eventType);
    }
}
