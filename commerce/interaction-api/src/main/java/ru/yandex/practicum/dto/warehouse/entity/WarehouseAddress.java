package ru.yandex.practicum.dto.warehouse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "warehouse_address")
public class WarehouseAddress {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "street", length = 200)
    private String street;

    @Column(name = "house", length = 20)
    private String house;

    @Column(name = "flat", length = 20)
    private String flat;
}