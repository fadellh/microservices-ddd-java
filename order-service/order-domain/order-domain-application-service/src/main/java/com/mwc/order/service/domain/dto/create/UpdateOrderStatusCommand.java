package com.mwc.order.service.domain.dto.create;


import com.mwc.domain.valueobject.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class UpdateOrderStatusCommand {
    @NotNull
    private UUID orderId;
    @NotNull
    private UUID adminId;
    @NotNull
    private OrderStatus orderStatus;
}
