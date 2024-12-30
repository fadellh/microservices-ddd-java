package com.mwc.inventory.service.domain.ports.output.repository;

import com.mwc.inventory.service.domain.entity.Inventory;

import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository {
    Optional<Inventory> findByIdAndWarehouseId(UUID inventoryId, UUID warehouseId);
    void updateQuantityByInventoryIdAndWarehouseId(Inventory inventory);
}
