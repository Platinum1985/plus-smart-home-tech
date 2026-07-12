package collector.controller;

import collector.service.SendAvroService;
import collector.service.EventService;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.collector.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.collector.HubEventProto;

import collector.mapper.proto.hub.HubProtoMapperService;
import collector.mapper.proto.sensor.SensorProtoMapperService;
import collector.model.hub.BaseDeviceEvent;
import collector.model.sensor.BaseSensorEvent;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final SensorProtoMapperService sensorMapperService;
    private final HubProtoMapperService hubMapperService;
    private final SendAvroService sendAvroService;

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {

//            Cериализация
            BaseSensorEvent domainEvent = sensorMapperService.mapFromProto(request);

//            Отправка в Avro
            sendAvroService.sendSensorEvent(domainEvent);
            log.info("Обработано событие датчика: {}", domainEvent.getClass().getSimpleName());

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Ошибка обработки события датчика", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal error: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
//            Сериализация
            BaseDeviceEvent domainEvent = hubMapperService.mapFromProto(request);
//            Отправка в Avro
            sendAvroService.sendHubEvent(domainEvent);
            log.info("Обработано событие хаба: {}", domainEvent.getClass().getSimpleName());

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Ошибка обработки события хаба", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal error: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }
}