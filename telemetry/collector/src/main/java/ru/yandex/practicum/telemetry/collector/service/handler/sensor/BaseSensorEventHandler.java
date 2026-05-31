package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.KafkaClient;
import ru.yandex.practicum.telemetry.collector.model.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.SensorEventType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Component
public abstract class BaseSensorEventHandler<E extends SensorEvent> {
    private final KafkaClient producer;
    protected final String topic = "telemetry.sensors.v1";

    protected BaseSensorEventHandler(KafkaClient producer) {
        this.producer = producer;
    }

    public abstract SensorEventType getMessageType();

    protected abstract SensorEventAvro mapToAvro(E event);

    @SuppressWarnings("unchecked")
    public void handle(SensorEvent event) {
        // 1. Проверяем тип события И КЛАСС объекта
        if (!getMessageType().equals(event.getType())) {
            throw new IllegalArgumentException("Unsupported event type: " + event.getType());
        }

        // 2. Теперь приведение безопасно
        E typedEvent = (E) event;

        // 3. Преобразуем в Avro
        SensorEventAvro avroEvent = mapToAvro(typedEvent);

        // 4. Сериализуем Avro‑объект в байты
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            SpecificDatumWriter<SensorEventAvro> writer =
                    new SpecificDatumWriter<>(SensorEventAvro.getClassSchema());
            Encoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
            writer.write(avroEvent, encoder);
            encoder.flush();
            byte[] serializedBytes = outputStream.toByteArray();

            // 5. Отправляем байты в Kafka через ProducerRecord
            Producer<String, byte[]> kafkaProducer = producer.getProducer();
            kafkaProducer.send(new ProducerRecord<>(topic, serializedBytes));
            log.info("Message sent to topic: {}, size: {} bytes", topic, serializedBytes.length);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize and send Avro message", e);
        }
    }
}
