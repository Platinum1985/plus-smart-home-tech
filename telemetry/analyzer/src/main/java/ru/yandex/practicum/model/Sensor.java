package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@Table(name = "sensors")
@AllArgsConstructor
@NoArgsConstructor
public class Sensor {
    @Id
    private String id;

    @Column(name = "hub_id", nullable = false)
    private String hubId;

    @OneToMany(mappedBy = "sensor")
    private List<ScenarioCondition> conditions = new ArrayList<>();

    @OneToMany(mappedBy = "sensor")
    private List<ScenarioAction> actions = new ArrayList<>();
}
