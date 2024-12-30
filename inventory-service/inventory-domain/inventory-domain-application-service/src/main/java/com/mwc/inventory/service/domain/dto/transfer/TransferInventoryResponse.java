package com.mwc.inventory.service.domain.dto.transfer;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class TransferInventoryResponse {
    private final UUID fromWarehouseId;
    private final UUID toWarehouseId;
    private final int fromQuantity;
    private final int toQuantity;
    private final boolean success;
    private final String message;
}
