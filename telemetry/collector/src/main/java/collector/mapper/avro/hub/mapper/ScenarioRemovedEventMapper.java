package collector.mapper.avro.hub.mapper;

import collector.model.hub.ScenarioRemovedEvent;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Component
public class ScenarioRemovedEventMapper implements HubAvroMapper<ScenarioRemovedEvent> {

    @Override
    public Class<ScenarioRemovedEvent> getEventType() {
        return ScenarioRemovedEvent.class;
    }

    @Override
    public void mapPayload(ScenarioRemovedEvent event, HubEventAvro avroEvent) {
        ScenarioRemovedEventAvro data = new ScenarioRemovedEventAvro();
        data.setName(event.getName());
        avroEvent.setPayload(data);
    }

}
