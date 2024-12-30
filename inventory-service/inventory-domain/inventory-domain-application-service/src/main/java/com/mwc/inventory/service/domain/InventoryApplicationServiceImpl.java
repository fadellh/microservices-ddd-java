package com.mwc.inventory.service.domain;

import com.mwc.inventory.service.domain.dto.transfer.TransferInventoryCommand;
import com.mwc.inventory.service.domain.dto.transfer.TransferInventoryResponse;
import com.mwc.inventory.service.domain.ports.input.message.service.InventoryApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;


@Service
@Validated
public class InventoryApplicationServiceImpl implements InventoryApplicationService {
    private InventoryTransferCommandHandler inventoryTransferCommandHandler;
    private InventoryTransferHelper inventoryTransferHelper;

    public InventoryApplicationServiceImpl(InventoryTransferCommandHandler inventoryTransferCommandHandler, InventoryTransferHelper inventoryTransferHelper) {
        this.inventoryTransferCommandHandler = inventoryTransferCommandHandler;
        this.inventoryTransferHelper = inventoryTransferHelper;
    }

    @Override
    public TransferInventoryResponse transferInventory(UUID inventoryId, TransferInventoryCommand transferInventoryCommand) {
        return inventoryTransferCommandHandler.transferInventory(inventoryId, transferInventoryCommand);
    }
}
