package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.apache.avro.generic.GenericRecord;
import ru.yandex.practicum.telemetry.collector.model.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.SensorEventType;
import ru.yandex.practicum.telemetry.collector.model.KafkaEventProducer;

public abstract class BaseSensorEventHandler<E extends SensorEvent> {
    private final KafkaEventProducer producer;
    protected final String topic = "telemetry.sensors.v1";

    protected BaseSensorEventHandler(KafkaEventProducer producer) {
        this.producer = producer;
    }

    public abstract SensorEventType getMessageType();

    protected abstract GenericRecord mapToAvro(E event);

    @SuppressWarnings("unchecked")
    public void handle(SensorEvent event) {
        // 1. Проверяем тип события И КЛАСС объекта
        if (!getMessageType().equals(event.getType())) {
            throw new IllegalArgumentException("Unsupported event type: " + event.getType());
        }

        // 2. Теперь приведение безопасно
        E typedEvent = (E) event;

        // 3. Преобразуем в Avro
        GenericRecord avroEvent = mapToAvro(typedEvent);

        // 4. Отправляем в Kafka
        producer.send(topic, avroEvent);
    }
}
