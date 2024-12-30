package com.mwc.inventory.service.dataaccess.inventory.command.mapper;

import com.mwc.domain.valueobject.InventoryId;
import com.mwc.domain.valueobject.ProductId;
import com.mwc.domain.valueobject.WarehouseId;
import com.mwc.inventory.service.dataaccess.inventory.command.entity.InventoryEntity;
import com.mwc.inventory.service.dataaccess.inventory.command.entity.InventoryItemEntity;
import com.mwc.inventory.service.domain.entity.Inventory;
import com.mwc.inventory.service.domain.valueobject.Quantity;
import org.springframework.stereotype.Component;

@Component
public class InventoryDataAccessMapper {

    public Inventory inventoryItemEntityToInventory(InventoryEntity inventoryEntity, InventoryItemEntity inventoryItemEntity) {
        return Inventory.builder()
                .id(new InventoryId(inventoryItemEntity.getInventory().getId()))
                .productId(new ProductId(inventoryEntity.getProductId()))
                .warehouseId(new WarehouseId(inventoryItemEntity.getWarehouseId()))
                .quantity(new Quantity(inventoryItemEntity.getQuantity()))
                .build();
    }

    public InventoryItemEntity inventoryToInventoryItemEntity(Inventory inventory) {
        return InventoryItemEntity.builder()
                .id(inventory.getId().getValue())
                .warehouseId(inventory.getWarehouseId().getValue())
                .quantity(inventory.getQuantity().getValue())
                .build();
    }
}
