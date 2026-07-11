package ru.yandex.practicum;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.ScenarioAction;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActionSender {

    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public void sendAction(String hubId, String scenarioName, ScenarioAction scenarioAction) {
        Action actionEntity = scenarioAction.getAction();

        DeviceActionProto actionProto = DeviceActionProto.newBuilder()
                .setSensorId(actionEntity.getSensorId())
                .setType(mapActionType(actionEntity.getType()))
                .setValue(actionEntity.getValue() != null ? actionEntity.getValue() : 0)
                .build();

        DeviceActionRequest request =
                DeviceActionRequest.newBuilder()
                        .setHubId(hubId)
                        .setScenarioName(scenarioName)
                        .setAction(actionProto)
                        .setTimestamp(Timestamp.newBuilder()
                                .setSeconds(Instant.now().getEpochSecond())
                                .build())
                        .build();

        log.info("Попытка отправить действие: hub={}, scenario={}, action={}", hubId, scenarioName, scenarioAction.getAction().getType());

        try {
            com.google.protobuf.Empty response = hubRouterClient.handleDeviceAction(request);
            log.info("Действие отправлено: сценарий={}, тип действия={}", scenarioName, actionEntity.getType());
        } catch (Exception e) {
            log.error("Не удалось отправить действие для сценария {}", scenarioName, e);
        }
    }

    private ActionTypeProto mapActionType(String type) {
        try {
            return ActionTypeProto.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Тип действия не найден: " + type);
        }
    }
}
