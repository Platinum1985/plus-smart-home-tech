package ru.yandex.practicum.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.consumer.Consumer;

public interface KafkaClient {
    KafkaProducer<String, byte[]> getProducer();
    Consumer<String, byte[]> getConsumer();
    void sendMessage(String topic, String key, byte[] value) throws InterruptedException, ExecutionException;
    void flush();
}
