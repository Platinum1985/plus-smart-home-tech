package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.apache.avro.generic.GenericRecord;
import ru.yandex.practicum.telemetry.collector.model.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.KafkaEventProducer;

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

        // 3. Преобразуем в Avro
        GenericRecord avroEvent = mapToAvro(typedEvent);

        // 4. Отправляем в Kafka
        producer.send(topic, avroEvent);
    }
}