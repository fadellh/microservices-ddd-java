package com.mwc.inventory.service.domain.dto.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class AutoTransferInventoryCommand {
    private UUID inventoryId;
    private int quantity;
    private Double latitude;
    private Double longitude;
    private UUID toWarehouseId;
    private UUID fromWarehouseId;
}
