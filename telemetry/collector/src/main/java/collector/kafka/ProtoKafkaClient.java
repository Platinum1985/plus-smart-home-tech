package collector.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import jakarta.annotation.PreDestroy;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProtoKafkaClient implements KafkaClient {

    private final String bootstrapServers = "localhost:9092";

    private final String acks = "all";

    private final int retries = 3;

    private final int lingerMs = 10;

    private final int bufferMemory = 33554432;

    private KafkaTemplate<String, byte[]> protoProducer;

    @Override
    public KafkaTemplate<String, byte[]> getProducer() {
        if (protoProducer == null) {
            protoProducer = new KafkaTemplate<>(
                    new DefaultKafkaProducerFactory<>(createProducerProps())
            );
        }
        return protoProducer;
    }

    @PreDestroy
    public void shutdown() {
        if (protoProducer != null) {
            try {
                protoProducer.flush();
            } catch (Exception e) {
                System.err.println("Error flushing proto producer: " + e.getMessage());
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
        props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        return props;
    }
}