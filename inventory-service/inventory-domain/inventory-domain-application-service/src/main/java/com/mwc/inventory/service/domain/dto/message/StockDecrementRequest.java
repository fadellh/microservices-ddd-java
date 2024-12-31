package com.mwc.inventory.service.domain.dto.message;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class StockDecrementRequest {
    private String id;
    private String sagaId;
    private UUID inventoryId;
    private UUID productId;
    private UUID warehouseId;
    private int quantity;
    private Instant createdAt;

}
