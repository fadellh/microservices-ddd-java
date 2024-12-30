package com.mwc.inventory.service.domain;

import com.mwc.inventory.service.domain.entity.Inventory;
import com.mwc.inventory.service.domain.event.StockDecrementedEvent;
import com.mwc.inventory.service.domain.event.StockIncrementedEvent;
import com.mwc.inventory.service.domain.ports.output.message.publisher.StockDecrementedMessagePublisher;
import com.mwc.inventory.service.domain.ports.output.message.publisher.StockIncrementedMessagePublisher;
import com.mwc.inventory.service.domain.ports.output.repository.InventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class InventoryTransferHelper {

    private final InventoryDomainService inventoryDomainService;

    private final InventoryRepository inventoryRepository;

    private final StockDecrementedMessagePublisher stockDecrementedMessagePublisher;
    private final StockIncrementedMessagePublisher stockIncrementedEventDomainEventPublisher;

    public InventoryTransferHelper(
            InventoryDomainService inventoryDomainService,
            InventoryRepository inventoryRepository,
            StockDecrementedMessagePublisher stockDecrementedMessagePublisher,
            StockIncrementedMessagePublisher stockIncrementedEventDomainEventPublisher
    ) {
        this.inventoryDomainService = inventoryDomainService;
        this.inventoryRepository = inventoryRepository;
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
        inventoryRepository.updateQuantityByInventoryIdAndWarehouseId(sourceInventory);
        inventoryRepository.updateQuantityByInventoryIdAndWarehouseId(destinationInventory);
    }

}
