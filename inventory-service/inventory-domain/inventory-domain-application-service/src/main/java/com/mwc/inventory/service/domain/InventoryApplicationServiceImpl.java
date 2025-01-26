package com.mwc.inventory.service.domain;

import com.mwc.inventory.service.domain.dto.product.ProductItemResponse;
import com.mwc.inventory.service.domain.dto.transfer.TransferInventoryCommand;
import com.mwc.inventory.service.domain.dto.transfer.TransferInventoryResponse;
import com.mwc.inventory.service.domain.ports.input.message.service.InventoryApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;


@Service
@Validated
public class InventoryApplicationServiceImpl implements InventoryApplicationService {
    private final InventoryTransferCommandHandler inventoryTransferCommandHandler;
    private final InventoryQueryHandler inventoryQueryHandler;

    public InventoryApplicationServiceImpl(InventoryTransferCommandHandler inventoryTransferCommandHandler, InventoryQueryHandler inventoryQueryHandler) {
        this.inventoryTransferCommandHandler = inventoryTransferCommandHandler;
        this.inventoryQueryHandler = inventoryQueryHandler;
    }

    @Override
    public TransferInventoryResponse transferInventory(UUID inventoryId, TransferInventoryCommand transferInventoryCommand) {
        return inventoryTransferCommandHandler.transferInventory(inventoryId, transferInventoryCommand);
    }

    @Override
    public List<ProductItemResponse> retrieveCatalogs() {
        return inventoryQueryHandler.getCatalogs();
    }

//    @Override
//    public TransferInventoryResponse transferInventory(UUID inventoryId, TransferInventoryCommand transferInventoryCommand) {
//
//        return inventoryTransferCommandHandler.autoTransferInventory(AutoTransferInventoryCommand.builder()
//                .inventoryId(inventoryId)
//                .fromWarehouseId(transferInventoryCommand.getFromWarehouseId())
//                .toWarehouseId(transferInventoryCommand.getToWarehouseId())
//                .quantity(transferInventoryCommand.getQuantity())
//                .latitude((-6.200000))
//                .longitude(106.816666)
//                .build());
//    }
}
