package com.mwc.inventory.service.domain.dto.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@Builder
public class TransferInventoryCommand {
    @NotNull
    private final String toWarehouseId;
    @NotNull
    private final int quantity;
}
