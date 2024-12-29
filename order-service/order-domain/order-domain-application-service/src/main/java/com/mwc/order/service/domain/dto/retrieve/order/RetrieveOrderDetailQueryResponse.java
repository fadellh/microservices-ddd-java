package com.mwc.order.service.domain.dto.retrieve.order;


import com.mwc.domain.valueobject.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class RetrieveOrderDetailQueryResponse {
    private final UUID orderNumber;
    private final UUID customerId;
    private final String customerAddress;
    private final BigDecimal totalAmount;
    private final BigDecimal shippingCost;
    private final OrderStatus orderStatus;
    private final OrderAddressQuery orderAddress;
    private final List<OrderItemQuery> items;

}
