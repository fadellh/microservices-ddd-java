package com.mwc.inventory.service.dataaccess.inventory.command.adapter;

import com.mwc.inventory.service.dataaccess.inventory.command.entity.InventoryEntity;
import com.mwc.inventory.service.dataaccess.inventory.command.entity.InventoryItemEntity;
import com.mwc.inventory.service.dataaccess.inventory.command.entity.StockJournalEntity;
import com.mwc.inventory.service.dataaccess.inventory.command.mapper.InventoryDataAccessMapper;
import com.mwc.inventory.service.dataaccess.inventory.command.repository.InventoryItemJpaRepository;
import com.mwc.inventory.service.dataaccess.inventory.command.repository.InventoryJpaRepository;
import com.mwc.inventory.service.dataaccess.inventory.command.repository.StockJournalJpaRepository;
import com.mwc.inventory.service.domain.entity.Inventory;
import com.mwc.inventory.service.domain.entity.StockJournal;
import com.mwc.inventory.service.domain.ports.output.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Qualifier("inventoryCommandRepository")
public class InventoryCommandRepositoryImpl implements InventoryRepository {

    private final InventoryJpaRepository inventoryJpaRepository;
    private final InventoryItemJpaRepository inventoryItemJpaRepository;
    private final InventoryDataAccessMapper inventoryDataAccessMapper;
    private final StockJournalJpaRepository stockJournalJpaRepository;


    public InventoryCommandRepositoryImpl(InventoryJpaRepository inventoryJpaRepository, InventoryItemJpaRepository inventoryItemJpaRepository, InventoryDataAccessMapper inventoryDataAccessMapper, StockJournalJpaRepository stockJournalJpaRepository) {
        this.inventoryJpaRepository = inventoryJpaRepository;
        this.inventoryItemJpaRepository = inventoryItemJpaRepository;
        this.inventoryDataAccessMapper = inventoryDataAccessMapper;
        this.stockJournalJpaRepository = stockJournalJpaRepository;
    }


    @Override
    public Optional<Inventory> findByIdAndWarehouseId(UUID inventoryId, UUID warehouseId) {
        Optional<InventoryEntity> inventoryEntity = inventoryJpaRepository.findById(inventoryId);
        if (inventoryEntity.isEmpty()) {
            return Optional.empty();
        }

        Optional<InventoryItemEntity> inventoryItemEntity = inventoryItemJpaRepository.findByInventoryIdAndWarehouseId(inventoryId, warehouseId);

        return inventoryItemEntity.map(itemEntity -> inventoryDataAccessMapper.inventoryItemEntityToInventory(inventoryEntity.get(), itemEntity));
    }

    @Override
    public void updateQuantityByInventoryIdAndWarehouseId(Inventory inventory) {
        int rowsUpdated = inventoryItemJpaRepository.updateQuantityByInventoryIdAndWarehouseId(inventory.getId().getValue(), inventory.getWarehouseId().getValue(), inventory.getQuantity().getValue());
        if (rowsUpdated == 0) {
            throw new IllegalArgumentException("Inventory item not found for the given inventoryId and warehouseId");
        }    }

    @Override
    public List<UUID> findWarehouseIdsByInventoryId(UUID inventoryId, UUID warehouseId) {
        return inventoryItemJpaRepository.findWarehouseIdsByInventoryId(inventoryId, warehouseId);
    }

    public void saveStockJournal(StockJournal stockJournal, Inventory inventory) {
        UUID inventoryItemId = inventory.getId().getValue();
        UUID warehouseId = inventory.getWarehouseId().getValue();

        Optional<InventoryItemEntity> inventoryItemEntity = inventoryItemJpaRepository.findByInventoryIdAndWarehouseId(inventoryItemId, warehouseId);
        if (inventoryItemEntity.isEmpty()) {
            throw new IllegalArgumentException("InventoryItemEntity not found for ID: " + inventoryItemId);
        }

        StockJournalEntity stockJournalEntity = inventoryDataAccessMapper.stockJournalToStockJournalEntity(stockJournal, inventoryItemEntity.get());
        stockJournalJpaRepository.save(stockJournalEntity);

    }
}
