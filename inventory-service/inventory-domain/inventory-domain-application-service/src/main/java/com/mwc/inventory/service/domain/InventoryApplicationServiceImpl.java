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
    private final InventoryTransferCommandHandler inventoryTransferCommandHandler;

    public InventoryApplicationServiceImpl(InventoryTransferCommandHandler inventoryTransferCommandHandler) {
        this.inventoryTransferCommandHandler = inventoryTransferCommandHandler;
    }

    @Override
    public TransferInventoryResponse transferInventory(UUID inventoryId, TransferInventoryCommand transferInventoryCommand) {
        return inventoryTransferCommandHandler.manualTransferInventory(inventoryId, transferInventoryCommand);
    }
}
