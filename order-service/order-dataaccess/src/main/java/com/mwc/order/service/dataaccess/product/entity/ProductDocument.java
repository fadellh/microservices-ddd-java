package com.mwc.order.service.dataaccess.product.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("products")
public class ProductDocument {
    @Id
    private Object _id;
    @Field("id")
    private String id;

    private String name;
    private BigDecimal price;
}
