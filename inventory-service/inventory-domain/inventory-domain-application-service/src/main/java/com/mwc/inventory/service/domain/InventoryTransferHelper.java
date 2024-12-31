package com.mwc.inventory.service.domain;

import com.mwc.inventory.service.domain.dto.transfer.AutoTransferInventoryCommand;
import com.mwc.inventory.service.domain.entity.Inventory;
import com.mwc.inventory.service.domain.event.StockDecrementedEvent;
import com.mwc.inventory.service.domain.event.StockIncrementedEvent;
import com.mwc.inventory.service.domain.ports.output.message.publisher.StockDecrementedMessagePublisher;
import com.mwc.inventory.service.domain.ports.output.message.publisher.StockIncrementedMessagePublisher;
import com.mwc.inventory.service.domain.ports.output.repository.InventoryRepository;
import com.mwc.inventory.service.domain.ports.output.repository.WarehouseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class InventoryTransferHelper {

    private final InventoryDomainService inventoryDomainService;

    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;

    private final StockDecrementedMessagePublisher stockDecrementedMessagePublisher;
    private final StockIncrementedMessagePublisher stockIncrementedEventDomainEventPublisher;

    public InventoryTransferHelper(
            InventoryDomainService inventoryDomainService,
            @Qualifier("inventoryCommandRepository") InventoryRepository inventoryRepository,
            WarehouseRepository warehouseRepository,
            StockDecrementedMessagePublisher stockDecrementedMessagePublisher,
            StockIncrementedMessagePublisher stockIncrementedEventDomainEventPublisher
    ) {
        this.inventoryDomainService = inventoryDomainService;
        this.inventoryRepository = inventoryRepository;
        this.warehouseRepository = warehouseRepository;
        this.stockDecrementedMessagePublisher = stockDecrementedMessagePublisher;
        this.stockIncrementedEventDomainEventPublisher = stockIncrementedEventDomainEventPublisher;
    }


    @Transactional
    public StockDecrementedEvent decreaseStock(UUID inventoryId, UUID warehouseId, int quantity) {
        Optional<Inventory> sourceInventory = inventoryRepository.findByIdAndWarehouseId(inventoryId, warehouseId);
        if (sourceInventory.isEmpty()) {
            throw new IllegalArgumentException("Inventory not found");
        }

        return inventoryDomainService.deductStock(sourceInventory.get(), quantity, stockDecrementedMessagePublisher);
    }

    @Transactional
    public StockIncrementedEvent increaseStock(UUID inventoryId, UUID warehouseId, int quantity) {
        Optional<Inventory>  destinationInventory = inventoryRepository.findByIdAndWarehouseId(inventoryId, warehouseId);
        if (destinationInventory.isEmpty()) {
            throw new IllegalArgumentException("Inventory not found");
        }
        return inventoryDomainService.incrementStock(destinationInventory.get(), quantity, stockIncrementedEventDomainEventPublisher);
    }

    @Transactional
    public void persistTransferStock(Inventory sourceInventory, Inventory destinationInventory) {
        // update quantity of source and destination inventory warehouse. Total quantity should be same before and after transfer
        inventoryRepository.updateQuantityByInventoryIdAndWarehouseId(sourceInventory);
        inventoryRepository.updateQuantityByInventoryIdAndWarehouseId(destinationInventory);
    }

    public Optional<UUID> findNearestWarehouse(AutoTransferInventoryCommand autoTransferInventoryCommand) {
        List<UUID> warehouseIds = inventoryRepository.findWarehouseIdsByInventoryId(autoTransferInventoryCommand.getInventoryId(), autoTransferInventoryCommand.getFromWarehouseId());
        // find nearest warehouse
        return warehouseRepository.findNearestWarehouseWithinIds(autoTransferInventoryCommand.getLatitude(), autoTransferInventoryCommand.getLongitude(), warehouseIds);
    }

}
