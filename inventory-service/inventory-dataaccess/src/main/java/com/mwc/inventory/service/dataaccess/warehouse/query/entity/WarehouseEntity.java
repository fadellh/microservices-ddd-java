package com.mwc.inventory.service.dataaccess.warehouse.query.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "warehouses", schema = "warehouse")
@Entity
public class WarehouseEntity {

    @Id
    private UUID id;
    private String name;
    // latitude and longitude
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;

}