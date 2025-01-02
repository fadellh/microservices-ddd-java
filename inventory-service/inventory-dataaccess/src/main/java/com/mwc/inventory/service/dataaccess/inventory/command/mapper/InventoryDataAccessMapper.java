package com.mwc.inventory.service.dataaccess.inventory.command.mapper;

import com.mwc.domain.valueobject.InventoryId;
import com.mwc.domain.valueobject.ProductId;
import com.mwc.domain.valueobject.WarehouseId;
import com.mwc.inventory.service.dataaccess.inventory.command.entity.InventoryEntity;
import com.mwc.inventory.service.dataaccess.inventory.command.entity.InventoryItemEntity;
import com.mwc.inventory.service.dataaccess.inventory.command.entity.StockJournalEntity;
import com.mwc.inventory.service.domain.entity.Inventory;
import com.mwc.inventory.service.domain.entity.StockJournal;
import com.mwc.inventory.service.domain.valueobject.Quantity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

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
                .id(inventory.getInventoryItemId())
                .inventory(InventoryEntity.builder().id(inventory.getId().getValue()).productId(inventory.getProductId().getValue()).build())
                .warehouseId(inventory.getWarehouseId().getValue())
                .quantity(inventory.getQuantity().getValue())
                .build();
    }

    public StockJournalEntity stockJournalToStockJournalEntity(StockJournal stockJournal, InventoryItemEntity inventoryItemEntity) {
        return StockJournalEntity.builder()
                .id(UUID.randomUUID())
                .inventoryItem(inventoryItemEntity)
                .totalQuantityChanged(0)
                .quantityChanged(stockJournal.getQuantityChanged().getValue())
                .reason(stockJournal.getReason())
                .type(stockJournal.getType())
                .createdAt(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()))
                .status(stockJournal.getStatus())
                .build();
    }

}
