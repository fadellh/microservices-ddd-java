package com.mwc.order.service.domain.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Getter
@Builder
@AllArgsConstructor
public class StockDecrementResponse {
    private final String id;
    private final String sagaId;
    private final UUID inventoryId;
    private final UUID orderId;
    private final Instant createdAt;
    private final List<String> failureMessage;
}
