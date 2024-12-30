package com.mwc.inventory.service.dataaccess.inventory.command.repository;

import com.mwc.inventory.service.dataaccess.inventory.command.entity.InventoryItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryItemJpaRepository extends JpaRepository<InventoryItemEntity, UUID> {
    Optional<InventoryItemEntity> findByInventoryIdAndWarehouseId(UUID inventoryId, UUID warehouseId);

    @Modifying
    @Transactional
    @Query("UPDATE InventoryItemEntity i SET i.quantity = :quantity, i.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE i.inventory.id = :inventoryId AND i.warehouseId = :warehouseId")
    int updateQuantityByInventoryIdAndWarehouseId(UUID inventoryId, UUID warehouseId, int quantity);
}
