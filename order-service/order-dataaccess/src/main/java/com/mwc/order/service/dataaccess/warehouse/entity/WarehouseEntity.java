package com.mwc.order.service.dataaccess.warehouse.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

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
}