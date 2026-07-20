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
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.deserialize.SensorEventDeserializer;
import ru.yandex.practicum.kafka.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

/**
 * Класс AggregationStarter, ответственный за запуск агрегации данных.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    @Value("${spring.kafka.topics.sensor:telemetry.sensors.v1}")
    private String sensorTopic;

    @Value("${spring.kafka.topics.snapshot:telemetry.snapshots.v1}")
    private String snapshotTopic;

    private final KafkaClient kafkaClient;
    private final SensorEventDeserializer sensorEventDeserializer;

    private Consumer<String, byte[]> consumer;
    private volatile boolean running = true;

    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public void start() {
        try {
            consumer = kafkaClient.getConsumer();
            consumer.subscribe(Collections.singleton(sensorTopic));

            // Отключаем авто-коммит
            consumer.commitSync(); // инициализация

            log.info("Агрегация запущена, слушаем топик: {}", sensorTopic);

            while (running) {
                ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, byte[]> record : records) {
                    SensorEventAvro event = sensorEventDeserializer.deserialize(record.topic(), record.value());
                    if (event != null) {
                        Optional<SensorsSnapshotAvro> updatedSnapshot = updateState(event);
                        if (updatedSnapshot.isPresent()) {
                            sendSnapshot(updatedSnapshot.get());
                        }
                    }
                }

                // Принудительно отправляем все буферизованные сообщения
                kafkaClient.getProducer().flush();

                // Коммитим offset только после успешной отправки всех снапшотов
                consumer.commitSync();
            }
        } catch (WakeupException e) {
            log.info("Получен сигнал завершения, останавливаемся");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        }
    }

    private Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        String hubId = event.getHubId();
        String sensorId = event.getId();
        long eventTimestamp = event.getTimestamp();

        SensorsSnapshotAvro snapshot = snapshots.get(hubId);
        if (snapshot == null) {
            snapshot = SensorsSnapshotAvro.newBuilder()
                    .setHubId(hubId)
                    .setTimestamp(eventTimestamp)
                    .setSensorsState(new HashMap<>())
                    .build();
            snapshots.put(hubId, snapshot);
        }

        Map<String, SensorStateAvro> sensors = snapshot.getSensorsState();
        SensorStateAvro oldState = sensors.get(sensorId);

        if (oldState != null && eventTimestamp < oldState.getTimestamp()) {
            log.debug("Игнорируем устаревшее событие для датчика {}", sensorId);
            return Optional.empty();
        }
        if (oldState != null && Objects.equals(oldState.getData(), event.getPayload())) {
            log.debug("Данные от датчика {} не изменились", sensorId);
            return Optional.empty(); // данные не изменились
        }

        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(eventTimestamp)
                .setData(event.getPayload())
                .build();

        sensors.put(sensorId, newState);
        SensorsSnapshotAvro updatedSnapshot = SensorsSnapshotAvro.newBuilder(snapshot)
                .setTimestamp(eventTimestamp)
                .setSensorsState(sensors)
                .build();

        snapshots.put(hubId, updatedSnapshot);
        log.info("Снапшот обновлен для хаба {}, датчик {}", hubId, sensorId);

        return Optional.of(updatedSnapshot);  // ← пишем в Kafka
    }

    /**
     * Отправляет снапшот в Kafka.
     */
    private void sendSnapshot(SensorsSnapshotAvro snapshot) {
        try {
            byte[] data = serializeToAvro(snapshot);
            // Синхронная отправка — ждём, пока сообщение попадёт в лидер-партицию
            kafkaClient.getProducer().send(snapshotTopic, snapshot.getHubId(), data).get();
            log.info("Снапшот отправлен для хаба {} с {} датчиками",
                    snapshot.getHubId(), snapshot.getSensorsState().size());
        } catch (Exception e) {
            log.error("Не удалось отправить снапшот для хаба {}", snapshot.getHubId(), e);
            throw new RuntimeException("Failed to send snapshot", e);
        }
    }

    private byte[] serializeToAvro(SensorsSnapshotAvro snapshot) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
        DatumWriter<SensorsSnapshotAvro> writer = new SpecificDatumWriter<>(SensorsSnapshotAvro.getClassSchema());
        writer.write(snapshot, encoder);
        encoder.flush();
        return outputStream.toByteArray();
    }

    public void stop() {
        running = false;
        if (consumer != null) {
            consumer.wakeup();
        }
        log.info("Агрегатор остановлен");
    }
}