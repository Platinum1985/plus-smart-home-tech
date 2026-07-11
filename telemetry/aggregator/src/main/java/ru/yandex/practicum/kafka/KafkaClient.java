package ru.yandex.practicum.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.kafka.core.KafkaTemplate;

public interface KafkaClient {

    KafkaTemplate<String, byte[]> getProducer();
    Consumer<String, byte[]> getConsumer();

}
