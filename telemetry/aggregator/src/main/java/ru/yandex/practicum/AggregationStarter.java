package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.deserialize.SensorEventDeserializer;
import ru.yandex.practicum.kafka.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;



@Component
@RequiredArgsConstructor
@Slf4j
public class AggregationStarter {

    private final KafkaClient kafkaClient;
    @Value("${spring.kafka.topics.snapshot:telemetry.snapshots.v1}")
    private final String snapshotTopic;

    // Десериализатор событий сенсора
    private final SensorEventDeserializer deserializer = new SensorEventDeserializer();

    /**
     * Запускает процесс агрегации данных
     */
    public void start() {
        log.info("Запуск агрегатора данных телеметрии...");

        try (Consumer<String, byte[]> consumer = kafkaClient.getConsumer()) {
            consumer.subscribe(List.of("sensor-events"));

            while (true) {
                ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(100));
                List<SensorEventAvro> events = new ArrayList<>();

                for (ConsumerRecord<String, byte[]> record : records) {
                    SensorEventAvro event = deserializer.deserialize(record.topic(), record.value());
                    events.add(event);
                }

                if (!events.isEmpty()) {
                    SensorsSnapshotAvro snapshot = aggregateEvents(events);
                    sendSnapshot(snapshot);
                }
            }
        } finally {
            kafkaClient.flush();
            log.info("Агрегация завершена");
        }
    }

    /**
     * Агрегирует список событий в снапшот состояния хаба
     */
    private SensorsSnapshotAvro aggregateEvents(List<SensorEventAvro> events) {
        // Здесь должна быть ваша логика агрегации
        // Например: группировка по hubId, вычисление средних значений и т. д.
        return SensorsSnapshotAvro.newBuilder()
                .setHubId("example-hub-id") // замените на реальную логику
                .build();
    }

    /**
     * Сериализует снапшот в Avro‑формат
     */
    private byte[] serializeToAvro(SensorsSnapshotAvro snapshot) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        DatumWriter<SensorsSnapshotAvro> writer = new SpecificDatumWriter<>(SensorsSnapshotAvro.class);

        writer.write(snapshot, encoder);
        encoder.flush();

        return out.toByteArray();
    }

    /**
     * Отправляет снапшот в Kafka
     */
    private void sendSnapshot(SensorsSnapshotAvro snapshot) {
        try {
            byte[] data = serializeToAvro(snapshot);
            kafkaClient.sendMessage(snapshotTopic, snapshot.getHubId(), data);
            log.info("Снапшот отправлен для хаба {} с {} датчиками",
                    snapshot.getHubId(), snapshot.getSensorsState().size());
        } catch (Exception e) {
            log.error("Не удалось отправить снапшот для хаба {}", snapshot.getHubId(), e);
            throw new RuntimeException("Failed to send snapshot", e);
        }
    }
}