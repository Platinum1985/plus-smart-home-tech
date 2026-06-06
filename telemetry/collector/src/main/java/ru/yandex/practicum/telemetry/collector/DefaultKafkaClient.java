package ru.yandex.practicum.telemetry.collector;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PreDestroy;

import java.util.HashMap;
import java.util.Map;

@Component
public class DefaultKafkaClient implements KafkaClient {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Producer<String, byte[]> producer;

    @Override
    public Producer<String, byte[]> getProducer() {
        if (producer == null) {
            producer = new KafkaProducer<>(createProducerProps());
        }
        return producer;
    }

    @PreDestroy
    public void shutdown() {
        if (producer != null) {
            try {
                producer.flush();
                Thread.sleep(500); // Таймаут 500 мс
                producer.close();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Восстанавливаем статус прерывания
                System.err.println("Поток был прерван во время ожидания: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Ошибка очистки producer: " + e.getMessage());
            }
        }
    }

    // Для бинарной сериализации
    private Map<String, Object> createProducerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);

        // Опционально: можно добавить настройки буфера, ретрисов и т. д.
        return props;
    }
}