package ru.yandex.practicum.telemetry.collector.model;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaEventProducer {

    private final KafkaTemplate<String, GenericRecord> kafkaTemplate;

    public KafkaEventProducer(KafkaTemplate<String, GenericRecord> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async
    public void send(String topic, GenericRecord event) {
        log.info("topic = {},  event = {}", topic, event);
        kafkaTemplate.send(topic, event);
    }
}
