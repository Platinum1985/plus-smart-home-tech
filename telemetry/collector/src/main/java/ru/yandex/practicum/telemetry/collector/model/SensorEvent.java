package ru.yandex.practicum.telemetry.collector.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LightSensorEvent.class, name = "LIGHT_SENSOR"),
        @JsonSubTypes.Type(value = MotionSensorEvent.class, name = "MOTION_SENSOR"),
        @JsonSubTypes.Type(value = SwitchSensorEvent.class, name = "SWITCH_SENSOR"),
        @JsonSubTypes.Type(value = TemperatureSensorEvent.class, name = "TEMPERATURE_SENSOR"),
        @JsonSubTypes.Type(value = ClimateSensorEvent.class, name = "CLIMATE_SENSOR")
})
public abstract class SensorEvent {
    @NotBlank
    private String id;
    @NotBlank
    private String hubId;
    private Instant timestamp = Instant.now();

    // Поле type для Jackson — добавляется автоматически при сериализации
    @NotNull
    private String type;

    public abstract SensorEventType getType();

    /**
     * Устанавливает тип события на основе перечисления.
     */
    public void setTypeFromEnum() {
        this.type = getType().name();
    }

    /**
     * Вызывается перед сериализацией
     */
    public String getTypeForSerialization() {
        return type;
    }
}