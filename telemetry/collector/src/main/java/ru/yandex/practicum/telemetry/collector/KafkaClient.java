package ru.yandex.practicum.telemetry.collector;

import org.apache.kafka.clients.producer.Producer;

public interface KafkaClient {
    Producer<String, byte[]> getProducer();
}
