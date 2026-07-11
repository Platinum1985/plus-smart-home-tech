package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@Entity
@Table(name = "scenarios")
@AllArgsConstructor
@NoArgsConstructor
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Unique
    @Column(name = "hub_id", nullable = false)
    private String hubId;

    @Unique
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ScenarioCondition> conditions = new HashSet<>();

    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ScenarioAction> actions = new HashSet<>();


}
