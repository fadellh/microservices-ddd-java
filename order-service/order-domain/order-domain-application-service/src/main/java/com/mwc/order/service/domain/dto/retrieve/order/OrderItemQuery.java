package com.mwc.order.service.domain.dto.retrieve.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class OrderItemQuery {
    private final UUID productId;
    private final String productName;
    private final BigDecimal price;
    private final Integer quantity;
    private final BigDecimal subTotal;
}