package ru.yandex.practicum.telemetry.collector.proto.hub_converters;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class HubEventConverterFactory {
    private final Map<HubEventProto.PayloadCase, HubEventConverter> converters;

    public HubEventConverterFactory(List<HubEventConverter> allConverters) {
        this.converters = allConverters.stream()
                .collect(Collectors.toMap(
                        HubEventConverter::getSupportedType,
                        Function.identity()
                ));
    }

    public Optional<HubEventConverter> getConverterFor(HubEventProto request) {
        HubEventProto.PayloadCase payloadCase = request.getPayloadCase();
        return Optional.ofNullable(converters.get(payloadCase));
    }
}
