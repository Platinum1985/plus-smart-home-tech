package ru.yandex.practicum.telemetry.collector.proto.sensor_converters;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.model.SensorEvent;

public interface SensorEventConverter {
    SensorEvent convert(SensorEventProto request);
    SensorEventProto.PayloadCase getSupportedType(); // метод для идентификации типа
}
