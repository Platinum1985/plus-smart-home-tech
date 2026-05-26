package ru.yandex.practicum.telemetry.collector.service;

import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.yandex.practicum.telemetry.collector.model.KafkaEventProducer;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class TelemetryCollectorApplication {
    public static void main(String[] args) {
        // Запускаем Spring Boot приложение
        ConfigurableApplicationContext context = SpringApplication.run(TelemetryCollectorApplication.class, args);

        // Настройка Kafka Producer после того, как Spring Boot уже запущен
        Map<String, Object> producerConfigs = new HashMap<>();
        producerConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        producerConfigs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerConfigs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaEventProducer.class);
        producerConfigs.put("schema.registry.url", "http://localhost:8080");

        ProducerFactory<String, GenericRecord> producerFactory =
                new DefaultKafkaProducerFactory<>(producerConfigs);

        KafkaTemplate<String, GenericRecord> kafkaTemplate =
                new KafkaTemplate<>(producerFactory);

        // Регистрируем бин KafkaEventProducer в Spring‑контексте
        context.getBeanFactory().registerSingleton(
                "kafkaEventProducer",
                new KafkaEventProducer(kafkaTemplate)
        );
    }
}