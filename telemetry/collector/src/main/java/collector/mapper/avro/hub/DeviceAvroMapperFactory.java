package collector.mapper.avro.hub;

import collector.mapper.avro.hub.mapper.HubAvroMapper;
import collector.model.hub.BaseDeviceEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DeviceAvroMapperFactory {

    private final Map<Class<? extends BaseDeviceEvent>, HubAvroMapper<?>> mappers;

    public DeviceAvroMapperFactory(List<HubAvroMapper<?>> mapperList) {
        this.mappers = mapperList.stream()
                .collect(Collectors.toMap(
                        HubAvroMapper::getEventType,
                        mapper -> mapper
                ));
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseDeviceEvent> HubAvroMapper<T> getMapper(Class<T> eventType) {
        HubAvroMapper<?> mapper = mappers.get(eventType);
        if (mapper == null) {
            throw new IllegalArgumentException(
                    "No mapper found for event type: " + eventType.getSimpleName()
            );
        }
        return (HubAvroMapper<T>) mapper;
    }

    public boolean hasMapper(Class<? extends BaseDeviceEvent> eventType) {
        return mappers.containsKey(eventType);
    }
}
