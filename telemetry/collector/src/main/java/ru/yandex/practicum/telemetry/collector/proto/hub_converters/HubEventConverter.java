package ru.yandex.practicum.telemetry.collector.proto.hub_converters;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.telemetry.collector.model.HubEvent;

public interface HubEventConverter {
    HubEvent convert(HubEventProto proto);
    HubEventProto.PayloadCase getSupportedType();
}
