package com.mwc.order.service.domain.dto.retrieve.order;

import com.mwc.domain.valueobject.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class RetrieveOrderQueryResponse {
    //give me simple response (order number, date, total price)
    private final UUID orderNumber;
    private final OrderStatus orderStatus;
    private final String orderDate;
    private final BigDecimal totalPrice;

}
