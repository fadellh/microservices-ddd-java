package com.mwc.inventory.service.domain.dto.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class TransferInventoryCommand {
    @NotNull
    private final UUID fromWarehouseId;
    @NotNull
    private final UUID toWarehouseId;
    @NotNull
    private final int quantity;
}
