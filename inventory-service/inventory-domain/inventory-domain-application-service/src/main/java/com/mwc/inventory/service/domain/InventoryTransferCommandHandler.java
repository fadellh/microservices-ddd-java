package com.mwc.inventory.service.domain;

import com.mwc.inventory.service.domain.dto.transfer.TransferInventoryCommand;
import com.mwc.inventory.service.domain.dto.transfer.TransferInventoryResponse;
import com.mwc.inventory.service.domain.mapper.InventoryDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class InventoryTransferCommandHandler {
    private InventoryTransferHelper inventoryTransferHelper;
    private InventoryDataMapper inventoryDataMapper;

    public InventoryTransferCommandHandler(
            InventoryTransferHelper inventoryTransferHelper,
            InventoryDataMapper inventoryDataMapper
    ) {
        this.inventoryTransferHelper = inventoryTransferHelper;
        this.inventoryDataMapper = inventoryDataMapper;
    }


    public TransferInventoryResponse transferInventory(UUID inventoryId, TransferInventoryCommand transferInventoryCommand) {
        inventoryTransferHelper.manualTransferInventory(inventoryId, transferInventoryCommand);

        return TransferInventoryResponse.builder()
                .build();
    }

}
