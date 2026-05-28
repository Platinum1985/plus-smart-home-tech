package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.collector.model.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.KafkaEventProducer;

@Slf4j
@Component
public abstract class BaseHubEventHandler<T extends HubEvent> {
    private final KafkaEventProducer producer;
    protected final String topic = "telemetry.hubs.v1";

    public BaseHubEventHandler(KafkaEventProducer producer) {
        this.producer = producer;
    }

    public abstract HubEventType getMessageType();

    // Теперь метод работает с конкретным типом T
    protected abstract GenericRecord mapToAvro(T event);

    @SuppressWarnings("unchecked")
    public void handle(HubEvent event) {
        // 1. Проверяем тип события И КЛАСС объекта
        if (!getMessageType().equals(event.getType())) {
            throw new IllegalArgumentException("Unsupported event type: " + event.getType());
        }

        // 2. Теперь приведение безопасно
        T typedEvent = (T) event;
        log.info("event in BaseHubEventHandler method handle typedEvent = {}", typedEvent);
        // 3. Преобразуем в Avro
        GenericRecord avroEvent = mapToAvro(typedEvent);
        log.info("avroEvent = {}", avroEvent);
        // 4. Отправляем в Kafka
        producer.send(topic, avroEvent);
    }
}