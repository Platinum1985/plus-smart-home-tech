package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.SnapshotEvaluationService;
import ru.yandex.practicum.deserialize.SnapshotDeserializer;
import ru.yandex.practicum.kafka.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor implements Runnable {

    @Value("${kafka.topics.snapshot:telemetry.snapshots.v1}")
    private String snapshotTopic;

    private final KafkaClient kafkaClient;
    private final SnapshotDeserializer snapshotDeserializer;
    private final SnapshotEvaluationService evaluationService;

    private Consumer<String, byte[]> consumer;
    private volatile boolean running = true;

    @Override
    public void run() {
        try {
            consumer = kafkaClient.getConsumer();
            consumer.subscribe(List.of(snapshotTopic));

            log.info("SnapshotProcessor запущен, слушаем топик: {}", snapshotTopic);

            while (running) {
                ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, byte[]> record : records) {
                    SensorsSnapshotAvro snapshot = snapshotDeserializer.deserialize(record.topic(), record.value());

                    if (snapshot == null) {
                        log.warn("Получен пустой снапшот из топика: {}", record.topic());
                        continue;
                    }

                    log.info("Получен снапшот для хаба: {}, количество датчиков: {}",
                            snapshot.getHubId(),
                            snapshot.getSensorsState().size());

                    evaluationService.evaluateSnapshot(snapshot);
                }

                consumer.commitSync();
            }
        } catch (Exception e) {
            log.error("Ошибка в SnapshotProcessor", e);
        } finally {
            if (consumer != null) {
                consumer.close();
                log.info("Consumer для топика {} закрыт", snapshotTopic);
            }
        }
    }

    public void stop() {
        running = false;
        if (consumer != null) {
            consumer.wakeup();
            log.info("Получен сигнал остановки SnapshotProcessor");
        }
    }
}