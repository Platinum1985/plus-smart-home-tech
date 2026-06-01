package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.KafkaClient;
import ru.yandex.practicum.telemetry.collector.model.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.HubEventType;

import java.io.ByteArrayOutputStream;

@Slf4j
@Component
public abstract class BaseHubEventHandler<T extends HubEvent> {
    private final KafkaClient producer;
    protected final String topic = "telemetry.hubs.v1";

    public BaseHubEventHandler(KafkaClient producer) {
        this.producer = producer;
    }

    public abstract HubEventType getMessageType();

    // Теперь метод работает с конкретным типом T
    protected abstract HubEventAvro mapToAvro(T event);

    @SuppressWarnings("unchecked")
    public void handle(HubEvent event) {
        // 1. Проверяем тип события И КЛАСС объекта
        if (!getMessageType().equals(event.getType())) {
            throw new IllegalArgumentException("Unsupported event type: " + event.getType());
        }
        log.info("event === {}", event);
        // 2. Теперь приведение безопасно
        T typedEvent = (T) event;
        log.info("typedEvent === {}", typedEvent);
        log.info("event in BaseHubEventHandler method handle typedEvent = {}", typedEvent);

        // 3. Преобразуем в Avro
        HubEventAvro avroEvent = mapToAvro(typedEvent);
        log.info("avroEvent = {}", avroEvent);

        // 4. Сериализуем Avro‑объект в байты
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            SpecificDatumWriter<HubEventAvro> writer = new SpecificDatumWriter<>(HubEventAvro.getClassSchema());
            Encoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
            writer.write(avroEvent, encoder);
            encoder.flush();
            byte[] serializedBytes = outputStream.toByteArray();

            // 5. Отправляем байты в Kafka через ProducerRecord
            Producer<String, byte[]> kafkaProducer = producer.getProducer();
            kafkaProducer.send(new ProducerRecord<>(topic, serializedBytes));
            log.info("Message sent to topic: {}, size: {} bytes", topic, serializedBytes.length);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize and send Avro message", e);
        }
    }
}