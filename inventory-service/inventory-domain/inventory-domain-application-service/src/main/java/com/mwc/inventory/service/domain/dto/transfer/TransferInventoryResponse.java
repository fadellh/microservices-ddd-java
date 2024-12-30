package com.mwc.inventory.service.domain.dto.transfer;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TransferInventoryResponse {
    private final String fromWarehouseId;
    private final String toWarehouseId;
    private final int quantity;
    private final boolean success;
    private final String message;
}
