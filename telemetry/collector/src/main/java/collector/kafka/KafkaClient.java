package collector.kafka;

import org.springframework.kafka.core.KafkaTemplate;

public interface KafkaClient {

    KafkaTemplate<String, byte[]> getProducer();

}
