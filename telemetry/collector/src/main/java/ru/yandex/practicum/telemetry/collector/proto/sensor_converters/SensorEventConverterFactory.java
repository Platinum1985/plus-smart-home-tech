package ru.yandex.practicum.telemetry.collector.proto.sensor_converters;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


// Фабрика конвертеров
@Component
public class SensorEventConverterFactory {
    private final Map<SensorEventProto.PayloadCase, SensorEventConverter> converters;

    public SensorEventConverterFactory(List<SensorEventConverter> allConverters) {
        this.converters = allConverters.stream()
                .collect(Collectors.toMap(
                        SensorEventConverter::getSupportedType,
                        Function.identity()
                ));
    }

    public Optional<SensorEventConverter> getConverterFor(SensorEventProto request) {
        SensorEventProto.PayloadCase payloadCase = request.getPayloadCase();
        return Optional.ofNullable(converters.get(payloadCase));
    }
}
