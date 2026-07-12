package collector.service;

import collector.kafka.AvroKafkaClient;
import collector.mapper.avro.AvroMapper;
import collector.model.sensor.BaseSensorEvent;
import collector.model.hub.BaseDeviceEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


@Log4j2
@Service
@RequiredArgsConstructor
public class SendAvroService {

    @Value("${kafka.topics.sensor:telemetry.sensors.v1}")
    private String sensorTopic;

    @Value("${kafka.topics.hub:telemetry.hubs.v1}")
    private String hubTopic;

    private final AvroKafkaClient avrokafkaClient;
    private final AvroMapper avroMapper;

    // Работа с топиком датчиков

    public void sendSensorEvent(BaseSensorEvent event) {
        SensorEventAvro avroRecord = avroMapper.mapSensorEventToAvro(event); // Маппинг в avro

        KafkaTemplate<String, byte[]> producer = avrokafkaClient.getProducer(); // Получение producer

        try {
            byte[] record = serializeSensorAvro(avroRecord); // Формирование записи

            producer.send( // Отправка записи
                    sensorTopic,
                    event.getHubId(),
                    record
            );

        } catch (IOException e) {
            log.error("KAFKA ERROR: {}", e.getMessage());
        }

    }

    // Работа с топиком хаба

    public void sendHubEvent(BaseDeviceEvent event) {
        HubEventAvro avroRecord = avroMapper.mapHubEventToAvro(event); // Маппинг в avro

        KafkaTemplate<String, byte[]> producer = avrokafkaClient.getProducer(); // Получение producer

        try {
            byte[] record = serializeHubAvro(avroRecord); // Формирование записи

            producer.send( // Отправка записи
                    hubTopic,
                    event.getHubId(),
                    record
            );

        } catch (IOException e) {
            log.error("KAFKA ERROR: {}", e.getMessage());
        }
    }

    private void flushProducer(Producer<String, SpecificRecordBase> producer) {
        try {
            producer.flush();
        } catch (Exception e) {
            log.error("Flush error", e);
        }
    }

    private byte[] serializeSensorAvro(SensorEventAvro event) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
        DatumWriter<SensorEventAvro> writer = new SpecificDatumWriter<>(SensorEventAvro.getClassSchema());
        writer.write(event, encoder);
        encoder.flush();
        return outputStream.toByteArray();
    }

    private byte[] serializeHubAvro(HubEventAvro event) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
        DatumWriter<HubEventAvro> writer = new SpecificDatumWriter<>(HubEventAvro.getClassSchema());
        writer.write(event, encoder);
        encoder.flush();
        return outputStream.toByteArray();
    }

}
