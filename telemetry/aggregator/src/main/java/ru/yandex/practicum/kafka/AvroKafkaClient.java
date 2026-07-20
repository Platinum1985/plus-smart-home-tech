package ru.yandex.practicum.kafka;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AvroKafkaClient implements KafkaClient {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.auto-offset-reset:earliest}")
    private String autoOffsetReset;

    @Value("${spring.kafka.consumer.enable-auto-commit:false}")
    private boolean enableAutoCommit;

    @Value("${spring.kafka.consumer.properties.session.timeout.ms:10000}")
    private int sessionTimeoutMs;

    @Value("${spring.kafka.consumer.properties.heartbeat.interval.ms:3000}")
    private int heartbeatIntervalMs;

    @Value("${spring.kafka.consumer.properties.max.poll.interval.ms:300000}")
    private int maxPollIntervalMs;

    @Value("${spring.kafka.consumer.properties.max.poll.records:100}")
    private int maxPollRecords;

    @Value("${spring.kafka.consumer.properties.request.timeout.ms:20000}")
    private int requestTimeoutMs;

    @Value("${spring.kafka.consumer.properties.reconnect.backoff.ms:50}")
    private int reconnectBackoffMs;

    @Value("${spring.kafka.consumer.properties.reconnect.backoff.max.ms:1000}")
    private int reconnectBackoffMaxMs;

    @Value("${spring.kafka.consumer.properties.fetch.min.bytes:1}")
    private int fetchMinBytes;

    @Value("${spring.kafka.consumer.properties.fetch.max.wait.ms:500}")
    private int fetchMaxWaitMs;

    @Value("${spring.kafka.consumer.properties.max.partition.fetch.bytes:1048576}")
    private int maxPartitionFetchBytes;

    @Value("${spring.kafka.producer.properties.acks:all}")
    private String acks;

    @Value("${spring.kafka.producer.properties.retries:3}")
    private int retries;

    private KafkaTemplate<String, byte[]> producer;
    private Consumer<String, byte[]> consumer;

    @Override
    public KafkaTemplate<String, byte[]> getProducer() {
        if (producer == null) {
            producer = new KafkaTemplate<>(
                    new DefaultKafkaProducerFactory<>(createProducerProps())
            );
            log.info("Kafka producer инициализирован");
        }
        return producer;
    }

    public Consumer<String, byte[]> getConsumer() {
        if (consumer == null) {
            consumer = new KafkaConsumer<>(createConsumerProps());
            log.info("Kafka consumer инициализирован с group.id: {}", groupId);
        }
        return consumer;
    }

    @PreDestroy
    public void shutdown() {
        if (producer != null) {
            try {
                producer.flush();
                log.info("Kafka producer успешно очищен");
            } catch (Exception e) {
                log.error("Ошибка очистки producer: {}", e.getMessage(), e);
            }
        }
        if (consumer != null) {
            try {
                consumer.close(Duration.ofSeconds(5));
                log.info("Kafka consumer успешно закрыт");
            } catch (Exception e) {
                log.error("Ошибка закрытия consumer: {}", e.getMessage(), e);
            }
        }
    }

    private Map<String, Object> createProducerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, acks);
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        return props;
    }

    private Map<String, Object> createConsumerProps() {
        Map<String, Object> props = new HashMap<>();

        // Основные настройки
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);

        // Оптимизированные настройки
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeoutMs);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, heartbeatIntervalMs);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs);
        props.put(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, reconnectBackoffMs);
        props.put(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, reconnectBackoffMaxMs);
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, fetchMinBytes);
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, fetchMaxWaitMs);
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetchBytes);

        return props;
    }
}