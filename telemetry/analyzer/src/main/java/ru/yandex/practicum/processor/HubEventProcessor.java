package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.deserialize.HubEventDeserializer;
import ru.yandex.practicum.kafka.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.HubEventUpdateService;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    @Value("${kafka.topics.hub:telemetry.hubs.v1}")
    private String hubTopic;

    private final KafkaClient kafkaClient;
    private final HubEventDeserializer hubEventDeserializer;
    private final HubEventUpdateService updateService;

    private Consumer<String, byte[]> consumer;
    private volatile boolean running = true;

    @Override
    public void run() {
        try {
            consumer = kafkaClient.getConsumer();
            consumer.subscribe(List.of(hubTopic));

            log.info("HubEventProcessor запущен, слушаем топик: {}", hubTopic);

            while (running) {
                ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(500));

                for (ConsumerRecord<String, byte[]> record : records) {
                    HubEventAvro event = hubEventDeserializer.deserialize(record.topic(), record.value());

                    if (event == null) {
                        log.warn("Получено пустое событие из топика: {}", record.topic());
                        continue;
                    }

                    updateService.processHubEvent(event);
                }

                consumer.commitSync();
            }
        } catch (Exception e) {
            log.error("Ошибка в HubEventProcessor", e);
        } finally {
            if (consumer != null) {
                consumer.close();
                log.info("Consumer для топика {} закрыт", hubTopic);
            }
        }
    }

    public void stop() {
        running = false;
        if (consumer != null) {
            consumer.wakeup();
            log.info("Получен сигнал остановки HubEventProcessor");
        }
    }
}