package collector.controller;

import collector.service.SendAvroService;
import collector.model.sensor.BaseSensorEvent;
import collector.model.hub.BaseDeviceEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class CollectorController {

    private final SendAvroService service;

    // Эндпоинт для обработки событий от датчиков
    @PostMapping("/sensors")
    public void processingEventsFromSensors(
            @RequestBody BaseSensorEvent event
            ) {
        service.sendSensorEvent(event);
    }

    // Эндпоинт для обработки событий от хаба
    @PostMapping("/hubs")
    public void processingEventsFromHub(
            @RequestBody BaseDeviceEvent deviceEvent
    ) {
        service.sendHubEvent(deviceEvent);
    }

}
