package collector.mapper.avro.hub.mapper;

import collector.model.DeviceAction;
import collector.model.ScenarioCondition;
import collector.model.hub.ScenarioAddedEvent;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScenarioAddedEventMapper implements HubAvroMapper<ScenarioAddedEvent> {

    @Override
    public Class<ScenarioAddedEvent> getEventType() {
        return ScenarioAddedEvent.class;
    }

    @Override
    public void mapPayload(ScenarioAddedEvent event, HubEventAvro avroEvent) {
        ScenarioAddedEventAvro data = new ScenarioAddedEventAvro();
        data.setName(event.getName());

        List<ScenarioConditionAvro> avroConditions = event.getConditions().stream()
                .map(this::mapConditionToAvro)
                .collect(Collectors.toList());
        data.setConditions(avroConditions);

        List<DeviceActionAvro> avroActions = event.getActions().stream()
                .map(this::mapActionToAvro)
                .collect(Collectors.toList());
        data.setActions(avroActions);

        avroEvent.setPayload(data);
    }

    private ScenarioConditionAvro mapConditionToAvro(ScenarioCondition condition) {
        ScenarioConditionAvro avro = new ScenarioConditionAvro();
        avro.setSensorId(condition.getSensorId());
        avro.setType(ConditionTypeAvro.valueOf(condition.getType().name()));
        avro.setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()));
        avro.setValue(condition.getValue());
        return avro;
    }

    private DeviceActionAvro mapActionToAvro(DeviceAction action) {
        DeviceActionAvro avro = new DeviceActionAvro();
        avro.setSensorId(action.getSensorId());
        avro.setType(ActionTypeAvro.valueOf(action.getType().name()));
        avro.setValue(action.getValue());
        return avro;
    }

}
