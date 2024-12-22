package com.mwc.order.service.domain.dto.create;

import com.mwc.domain.valueobject.OrderStatus;
import lombok.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CreateOrderResponse {
    @NotNull
    private final OrderStatus orderStatus;
    @NotNull
    private final String message;
}
