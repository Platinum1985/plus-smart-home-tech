package collector.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import jakarta.annotation.PreDestroy;

import java.util.HashMap;
import java.util.Map;

@Component
public class AvroKafkaClient implements KafkaClient {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private KafkaTemplate<String, byte[]> producer;

    @Override
    public KafkaTemplate<String, byte[]> getProducer() {
        if (producer == null) {
            producer = new KafkaTemplate<>(
                    new DefaultKafkaProducerFactory<>(createProducerProps())
            );
        }
        return producer;
    }

    @PreDestroy
    public void shutdown() {
        if (producer != null) {
            try {
                producer.flush();
            } catch (Exception e) {
                System.err.println("Ошибка очистки producer: " + e.getMessage());
            }
        }
    }

//    Для бинарной сериализации
    private Map<String, Object> createProducerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        return props;
    }

}