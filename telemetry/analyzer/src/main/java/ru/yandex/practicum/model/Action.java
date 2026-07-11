package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "actions")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sensor_id", nullable = false)
    private String sensorId;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "value")
    private Integer value;

    @OneToMany(mappedBy = "action")
    private List<ScenarioAction> scenarioActions = new ArrayList<>();

}
