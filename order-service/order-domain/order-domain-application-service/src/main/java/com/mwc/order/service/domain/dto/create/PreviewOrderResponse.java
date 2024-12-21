package com.mwc.order.service.domain.dto.create;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class PreviewOrderResponse {
    private final BigDecimal totalAmount; // Total amount of the order
    private final BigDecimal shippingCost; // Cost of shipping (if applicable)
    private final BigDecimal discount; // Discount applied (if applicable)
    private final List<OrderItemResponse> items; // List of item details

    @Getter
    @AllArgsConstructor
    @Builder
    public static class OrderItemResponse {
        private final String productName;
        private final UUID productId;
        private final Integer quantity;
        private final BigDecimal price;
        private final BigDecimal subTotal;
    }
}
