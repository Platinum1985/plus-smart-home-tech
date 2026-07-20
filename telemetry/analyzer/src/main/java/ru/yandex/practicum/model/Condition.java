package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.grpc.telemetry.collector.ConditionOperationProto;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@Table(name = "conditions")
@AllArgsConstructor
@NoArgsConstructor
public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "operation", nullable = false)
    private ConditionOperationProto operation;

    @Column(name = "value")
    private Integer value;

    @OneToMany(mappedBy = "condition")
    private List<ScenarioCondition> scenarioConditions = new ArrayList<>();

}
