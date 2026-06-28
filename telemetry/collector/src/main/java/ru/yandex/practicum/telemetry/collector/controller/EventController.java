package ru.yandex.practicum.telemetry.collector.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.telemetry.collector.model.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.SensorEventType;
import ru.yandex.practicum.telemetry.collector.service.handler.hub.BaseHubEventHandler;
import ru.yandex.practicum.telemetry.collector.service.handler.sensor.BaseSensorEventHandler;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/events", consumes = MediaType.APPLICATION_JSON_VALUE)
public class EventController {

    // Карты для быстрого поиска обработчиков по типу события
    private final Map<HubEventType, BaseHubEventHandler<?>> hubHandlers;
    private final Map<SensorEventType, BaseSensorEventHandler<?>> sensorHandlers;

    public EventController(
            List<BaseHubEventHandler<?>> allHubHandlers,
            List<BaseSensorEventHandler<?>> allSensorHandlers
    ) {
        this.hubHandlers = allHubHandlers.stream()
                .collect(Collectors.toMap(
                        BaseHubEventHandler::getMessageType,
                        Function.identity()
                ));
        this.sensorHandlers = allSensorHandlers.stream()
                .collect(Collectors.toMap(
                        BaseSensorEventHandler::getMessageType,
                        Function.identity()
                ));
    }

    @PostMapping("/hubs")
    public ResponseEntity<String> collectHubEvent(@RequestBody HubEvent request) {
        log.info("REQUEST in controller === {}", request);
        try {
            // Ищем обработчик для типа события
            BaseHubEventHandler<?> handler = hubHandlers.get(request.getType());
            log.info("HANDLER in controller ==== {}", handler);
            if (handler == null) {
                log.warn("No handler found for hub event type: {}", request.getType());
                return ResponseEntity
                        .badRequest()
                        .body("Unsupported hub event type: " + request.getType());
            }

            // Передаём событие на обработку
            handler.handle(request);

            /*  log.info("Hub event processed: type={}, deviceId={}",
                    request.getType(),
                    ((HubEventWithDeviceId) request).getDeviceId()
            ); */

            return ResponseEntity.ok("Hub event processed successfully");

        } catch (IllegalArgumentException e) {
            log.warn("Validation error for hub event: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error processing hub event", e);
            return ResponseEntity
                    .status(500)
                    .body("Internal server error");
        }
    }

    @PostMapping("/sensors")
    public ResponseEntity<String> collectSensorEvent(@RequestBody SensorEvent request) {
        try {
            // Ищем обработчик для события сенсора
            BaseSensorEventHandler<?> handler = sensorHandlers.get(request.getType());

            if (handler == null) {
                log.warn("No handler found for sensor event type: {}", request.getType());
                return ResponseEntity
                        .badRequest()
                        .body("Unsupported sensor event type: " + request.getType());
            }

            // Передаём событие на обработку
            handler.handle(request);

            log.info("Sensor event processed: type={}", request.getType());

            return ResponseEntity.ok("Sensor event processed successfully");

        } catch (IllegalArgumentException e) {
            log.warn("Validation error for sensor event: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error processing sensor event", e);
            return ResponseEntity
                    .status(500)
                    .body("Internal server error");
        }
    }

    @GetMapping("/hub-handlers")
    public ResponseEntity<Map<HubEventType, String>> getHubHandlers() {
        // Создаём мап, где вместо обработчиков — их классы (для сериализации)
        Map<HubEventType, String> handlerNames = hubHandlers.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getClass().getSimpleName()
                ));

        return ResponseEntity.ok(handlerNames);
    }

    @GetMapping("/sensor-handlers")
    public ResponseEntity<Map<SensorEventType, String>> getSensorHandlers() {
        // Аналогично — заменяем обработчики на названия их классов
        Map<SensorEventType, String> handlerNames = sensorHandlers.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getClass().getSimpleName()
                ));

        return ResponseEntity.ok(handlerNames);
    }
}
