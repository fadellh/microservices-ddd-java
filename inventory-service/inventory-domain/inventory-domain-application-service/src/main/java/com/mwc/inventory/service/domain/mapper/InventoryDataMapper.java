package com.mwc.inventory.service.domain.mapper;

import com.mwc.inventory.service.domain.dto.transfer.TransferInventoryResponse;
import com.mwc.inventory.service.domain.entity.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryDataMapper {

    public TransferInventoryResponse inventoryToTransferInventoryResponse(Inventory source, Inventory destination) {
        return TransferInventoryResponse.builder()
                .fromWarehouseId(source.getWarehouseId().getValue())
                .toWarehouseId(destination.getWarehouseId().getValue())
                .fromQuantity(source.getQuantity().getValue())
                .toQuantity(destination.getQuantity().getValue())
                .success(true)
                .message("Inventory transfer is successful")
                .build();
    }
}
