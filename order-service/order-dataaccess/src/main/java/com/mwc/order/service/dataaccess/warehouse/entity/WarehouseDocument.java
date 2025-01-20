package com.mwc.order.service.dataaccess.warehouse.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("warehouses")
public class WarehouseDocument {

    @Id
    private Object _id;
    @Field("id")
    private UUID id;

    private String name;
    private Double latitude;
    private Double longitude;

}
