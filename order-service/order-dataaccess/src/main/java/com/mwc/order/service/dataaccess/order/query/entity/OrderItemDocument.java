package com.mwc.order.service.dataaccess.order.query.entity;


import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDocument {
    private UUID productId;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subTotal;
}
