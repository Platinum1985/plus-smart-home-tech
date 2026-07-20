package collector.service;

import collector.kafka.KafkaClient;
import collector.kafka.ProtoKafkaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.collector.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.collector.SensorEventProto;

@Log4j2
@Service
@RequiredArgsConstructor
public class EventService {

    private final ProtoKafkaClient protoKafkaClient;

    @Value("${kafka.topics.sensor:telemetry.sensors.v1}")
    private String sensorTopic;

    @Value("${kafka.topics.hub:telemetry.hubs.v1}")
    private String hubTopic;

    public void sendSensorEvent(SensorEventProto event) {
        KafkaTemplate<String, byte[]> producer = protoKafkaClient.getProducer();
        try {
            byte[] data = event.toByteArray(); // Proto сериализация
            producer.send(sensorTopic, event.getHubId(), data);
            log.info("Sent sensor event to topic {}: {}", sensorTopic, event);
        } catch (Exception e) {
            log.error("Failed to send sensor event to Kafka", e);
        }
    }

    public void sendHubEvent(HubEventProto event) {
        KafkaTemplate<String, byte[]> producer = protoKafkaClient.getProducer();
        try {
            byte[] data = event.toByteArray(); // Proto сериализация
            producer.send(hubTopic, event.getHubId(), data);
            log.info("Sent hub event to topic {}: {}", hubTopic, event);
        } catch (Exception e) {
            log.error("Failed to send hub event to Kafka", e);
        }
    }
}
