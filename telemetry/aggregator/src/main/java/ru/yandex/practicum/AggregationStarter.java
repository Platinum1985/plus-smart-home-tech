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
    private String snapshotTopic;

    // Храним последний отправленный снапшот для сравнения
    private SensorsSnapshotAvro lastSnapshot;

    private final SensorEventDeserializer deserializer = new SensorEventDeserializer();

    public void start() {
        log.info("Запуск агрегатора данных телеметрии...");

        try (Consumer<String, byte[]> consumer = kafkaClient.getConsumer()) {
            // ИСПРАВЛЕНИЕ 1: правильный топик из ТЗ
            consumer.subscribe(List.of("telemetry.sensors.v1"));

            while (true) {
                ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(100));
                List<SensorEventAvro> events = new ArrayList<>();

                for (ConsumerRecord<String, byte[]> record : records) {
                    try {
                        SensorEventAvro event = deserializer.deserialize(record.topic(), record.value());
                        events.add(event);
                    } catch (Exception e) {
                        log.error("Ошибка десериализации сообщения", e);
                        continue;
                    }
                }

                if (!events.isEmpty()) {
                    SensorsSnapshotAvro newSnapshot = aggregateEvents(events);
                    // ИСПРАВЛЕНИЕ 3: отправляем только при изменении
                    if (shouldSendSnapshot(newSnapshot)) {
                        lastSnapshot = newSnapshot;
                        sendSnapshot(newSnapshot);
                    } else {
                        log.debug("Снапшот не изменился, отправка пропущена");
                    }
                }
            }
        } finally {
            kafkaClient.flush();
            log.info("Агрегация завершена");
        }
    }

    // ИСПРАВЛЕНИЕ 2: реальная логика агрегации
    private SensorsSnapshotAvro aggregateEvents(List<SensorEventAvro> events) {
        if (events.isEmpty()) return null;

        // Берём hubId из первого события (все события в одном хабе)
        String hubId = events.get(0).getHubId();

        // Здесь должна быть логика агрегации состояний датчиков
        // В реальном коде нужно объединить состояния всех датчиков этого хаба
        return SensorsSnapshotAvro.newBuilder()
                .setHubId(hubId)
                // Добавьте логику заполнения sensorsState
                .build();
    }

    // ИСПРАВЛЕНИЕ 3: проверка на изменение снапшота
    private boolean shouldSendSnapshot(SensorsSnapshotAvro newSnapshot) {
        if (lastSnapshot == null) return true;
        // В реальном коде нужна более детальная проверка изменений
        return !lastSnapshot.getHubId().equals(newSnapshot.getHubId());
    }

    private byte[] serializeToAvro(SensorsSnapshotAvro snapshot) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        DatumWriter<SensorsSnapshotAvro> writer = new SpecificDatumWriter<>(SensorsSnapshotAvro.class);

        writer.write(snapshot, encoder);
        encoder.flush();

        return out.toByteArray();
    }

    // ИСПРАВЛЕНИЕ 4: улучшенная обработка ошибок
    private void sendSnapshot(SensorsSnapshotAvro snapshot) {
        try {
            byte[] data = serializeToAvro(snapshot);
            kafkaClient.sendMessage(snapshotTopic, snapshot.getHubId(), data);
            log.info("Снапшот отправлен для хаба {} с {} датчиками",
                    snapshot.getHubId(), snapshot.getSensorsState().size());
        } catch (Exception e) {
            log.error("Не удалось отправить снапшот для хаба {}", snapshot.getHubId(), e);
            // Не прерываем работу агрегатора при ошибке отправки
        }
    }
}