package com.mwc.order.service.domain.dto.create;

import com.mwc.domain.valueobject.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UpdateOrderStatusResponse {
    private final String message;
    private final OrderStatus orderStatus;
}
