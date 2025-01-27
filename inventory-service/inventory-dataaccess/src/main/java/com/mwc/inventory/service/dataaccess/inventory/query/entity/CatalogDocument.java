package com.mwc.inventory.service.dataaccess.inventory.query.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("catalogs")
public class CatalogDocument {
    @Id
    private ObjectId _id;
    @Field("id")
    private UUID id;

    private String name;
    private String brand;

    private String image;
    private String size;
    private List<String> availableColors;

    private BigDecimal price;
    private Integer maxQuantity;
    private Integer quantity;

}
