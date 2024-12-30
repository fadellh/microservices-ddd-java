package com.mwc.inventory.service.domain.ports.input.message.service;

import com.mwc.inventory.service.domain.dto.transfer.TransferInventoryCommand;
import com.mwc.inventory.service.domain.dto.transfer.TransferInventoryResponse;

import java.util.UUID;

public interface InventoryApplicationService {
    TransferInventoryResponse transferInventory(UUID inventoryId, TransferInventoryCommand transferInventoryCommand);
}
