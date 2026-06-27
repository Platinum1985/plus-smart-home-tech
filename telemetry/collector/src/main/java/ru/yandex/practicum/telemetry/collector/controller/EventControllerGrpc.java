package ru.yandex.practicum.telemetry.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import io.grpc.stub.StreamObserver;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.model.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.SensorEventType;
import ru.yandex.practicum.telemetry.collector.proto.hub_converters.HubEventConverter;
import ru.yandex.practicum.telemetry.collector.proto.hub_converters.HubEventConverterFactory;
import ru.yandex.practicum.telemetry.collector.proto.sensor_converters.SensorEventConverter;
import ru.yandex.practicum.telemetry.collector.proto.sensor_converters.SensorEventConverterFactory;
import ru.yandex.practicum.telemetry.collector.service.handler.hub.BaseHubEventHandler;
import ru.yandex.practicum.telemetry.collector.service.handler.sensor.BaseSensorEventHandler;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class EventControllerGrpc extends CollectorControllerGrpc.CollectorControllerImplBase {
    // Карты для быстрого поиска обработчиков по типу события
    private final Map<HubEventType, BaseHubEventHandler<?>> hubHandlers;
    private final Map<SensorEventType, BaseSensorEventHandler<?>> sensorHandlers;
    private final SensorEventConverterFactory converterFactory;
    private final HubEventConverterFactory hubConverterFactory;

    public EventControllerGrpc(
            List<BaseHubEventHandler<?>> allHubHandlers,
            List<BaseSensorEventHandler<?>> allSensorHandlers, SensorEventConverterFactory converterFactory, HubEventConverterFactory hubConverterFactory
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
        this.converterFactory = converterFactory;
        this.hubConverterFactory = hubConverterFactory;
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            SensorEventConverter converter = converterFactory.getConverterFor(request)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No converter for sensor type: " + request.getPayloadCase()));

            // Преобразуем Protobuf‑объект в доменную модель
            SensorEvent domainEvent = converter.convert(request);

            // Устанавливаем тип для сериализации (если нужно)
            domainEvent.setTypeFromEnum();
            BaseSensorEventHandler<?> handler = sensorHandlers.get(domainEvent.getType());
            log.info("HANDLER in controller ==== {}", handler);

            if (handler == null) {
                log.warn("No handler found for hub event type: {}", domainEvent.getType());
                log.info("Unsupported hub event type: " + domainEvent.getType());

                // Не вызываем handler.handle(), так как обработчика нет
                // Можно добавить ответ клиенту о неподдерживаемом типе события
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Unsupported sensor event type: " + domainEvent.getType())
                        .asRuntimeException());
            } else {
                // Обработчик найден — выполняем обработку
                handler.handle(domainEvent);
                log.info("Обработано событие датчика: {}", domainEvent.getClass().getSimpleName());
                responseObserver.onNext(Empty.getDefaultInstance());
                responseObserver.onCompleted();
            }

        } catch (Exception e) {
            log.error("Ошибка обработки события датчика", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal error: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            // Определяем конвертер для HubEvent
            HubEventConverter converter = hubConverterFactory.getConverterFor(request)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No converter for hub event type: " + request.getPayloadCase()));

            // Преобразуем Protobuf-объект в доменную модель
            HubEvent domainEvent = converter.convert(request);

            // Устанавливаем тип для сериализации (если нужно)
            domainEvent.setTypeFromEnum();
            BaseHubEventHandler<?> handler = hubHandlers.get(domainEvent.getType());
            log.info("HANDLER in controller ==== {}", handler);

            if (handler == null) {
                log.warn("No handler found for hub event type: {}", domainEvent.getType());
                log.info("Unsupported hub event type: " + domainEvent.getType());

                // Не вызываем handler.handle(), так как обработчика нет
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Unsupported hub event type: " + domainEvent.getType())
                        .asRuntimeException());
            } else {
                // Обработчик найден — выполняем обработку
                handler.handle(domainEvent);
                log.info("Обработано событие хаба: {}", domainEvent.getClass().getSimpleName());
                responseObserver.onNext(Empty.getDefaultInstance());
                responseObserver.onCompleted();
            }
        } catch (Exception e) {
            log.error("Ошибка обработки события хаба", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal error: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }
}