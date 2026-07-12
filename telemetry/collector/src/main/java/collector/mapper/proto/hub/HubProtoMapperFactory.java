package collector.mapper.proto.hub;

import collector.mapper.proto.hub.mapper.HubProtoMapper;
import collector.model.hub.BaseDeviceEvent;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class HubProtoMapperFactory {
    private final Map<Class<? extends BaseDeviceEvent>, HubProtoMapper<?>> mappers;

    public HubProtoMapperFactory(List<HubProtoMapper<?>> mapperList) {
        this.mappers = mapperList.stream()
                .collect(Collectors.toMap(HubProtoMapper::getEventType, mapper -> mapper));
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseDeviceEvent> HubProtoMapper<T> getMapper(Class<T> eventType) {
        HubProtoMapper<?> mapper = mappers.get(eventType);
        if (mapper == null) {
            throw new IllegalArgumentException("No proto mapper found for event type: " + eventType.getSimpleName());
        }
        return (HubProtoMapper<T>) mapper;
    }

    public boolean hasMapper(Class<? extends BaseDeviceEvent> eventType) {
        return mappers.containsKey(eventType);
    }
}