package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.apache.avro.generic.GenericRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.collector.model.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.KafkaEventProducer;
import ru.yandex.practicum.telemetry.collector.model.ScenarioRemovedEvent;


@Component
public class ScenarioRemovedHubEventHandler extends BaseHubEventHandler<ScenarioRemovedEvent> {

    public ScenarioRemovedHubEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_REMOVED;
    }

    @Override
    protected GenericRecord mapToAvro(ScenarioRemovedEvent event) {
        return ScenarRemovedEventAvro.newBuilder()
                .setId(event.getId())
                .build();
    }
}
