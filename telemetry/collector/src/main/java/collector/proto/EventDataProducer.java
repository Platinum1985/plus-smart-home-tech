package collector.proto;

import collector.model.sensor.BaseSensorEvent;
import collector.model.sensor.TemperatureSensorEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.collector.SensorEventProto;
import collector.mapper.proto.sensor.SensorProtoMapperService;

@Log4j2
@Component
@RequiredArgsConstructor
public class EventDataProducer {

    private final SensorProtoMapperService sensorProtoMapperService;

    @GrpcClient("collector")
    private CollectorControllerGrpc.CollectorControllerBlockingStub collectorStub;

    public void sendSensorEvent(BaseSensorEvent event) {
        try {
            SensorEventProto protoEvent = sensorProtoMapperService.mapToProto(event);

            sendEvent(protoEvent);
            log.info("Событие отправлено: id={}, type={}",
                    event.getId(), event.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("Ошибка отправки события: {}", event.getId(), e);
            throw e;
        }
    }

    public void sendRandomTemperatureEvent(TemperatureSensorEvent template) {
        int temperatureCelsius = getRandomSensorValue(
                template.getTemperatureC(),
                template.getTemperatureC() + 5
        );

        TemperatureSensorEvent event = new TemperatureSensorEvent();
        event.setId(template.getId());
        event.setHubId(template.getHubId());
        event.setTimestamp(java.time.Instant.now());
        event.setTemperatureC(temperatureCelsius);
        event.setTemperatureF(celsiusToFahrenheit(temperatureCelsius));

        sendSensorEvent(event);
    }

    private void sendEvent(SensorEventProto event) {
        log.debug("Отправляю Proto-событие: {}", event.getId());
//         Empty response = collectorStub.collectSensorEvent(event);
//         log.debug("Получил ответ: {}", response);
    }

    private int getRandomSensorValue(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    private int celsiusToFahrenheit(int celsius) {
        return (int) (celsius * 1.8 + 32);
    }
}