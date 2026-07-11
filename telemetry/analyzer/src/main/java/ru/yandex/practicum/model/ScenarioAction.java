package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "scenario_actions")
public class ScenarioAction {

    @EmbeddedId
    private ScenarioActionId id;

    @ManyToOne
    @MapsId("scenarioId")
    @JoinColumn(name = "scenario_id", nullable = false)
    private Scenario scenario;

    @ManyToOne
    @MapsId("sensorId")
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @ManyToOne
    @MapsId("actionId")
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;

    @Embeddable
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScenarioActionId implements Serializable {
        @Column(name = "scenario_id")
        private Long scenarioId;

        @Column(name = "sensor_id")
        private String sensorId;

        @Column(name = "action_id")
        private Long actionId;
    }
}
