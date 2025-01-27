package com.mwc.order.service.dataaccess.order.query.entity;


import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class OrderItemDocument {
    private String name;
    private BigDecimal price;
    private UUID productId;
    private UUID inventoryId;
    private Integer quantity;
    private BigDecimal subTotal;
}
