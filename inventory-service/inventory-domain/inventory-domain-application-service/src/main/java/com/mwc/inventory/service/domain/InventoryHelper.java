package com.mwc.inventory.service.domain;

import com.mwc.inventory.service.domain.dto.transfer.AutoTransferInventoryCommand;
import com.mwc.inventory.service.domain.dto.transfer.StockTransferEventResult;
import com.mwc.inventory.service.domain.dto.transfer.TransferInventoryCommand;
import com.mwc.inventory.service.domain.entity.Inventory;
import com.mwc.inventory.service.domain.entity.Warehouse;
import com.mwc.inventory.service.domain.event.StockDecrementedEvent;
import com.mwc.inventory.service.domain.event.StockIncrementedEvent;
import com.mwc.inventory.service.domain.ports.output.message.publisher.StockDecrementedMessagePublisher;
import com.mwc.inventory.service.domain.ports.output.message.publisher.StockIncrementedMessagePublisher;
import com.mwc.inventory.service.domain.ports.output.repository.InventoryRepository;
import com.mwc.inventory.service.domain.ports.output.repository.WarehouseRepository;
import com.mwc.inventory.service.domain.valueobject.StockJournalReason;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class InventoryHelper {

    private final InventoryDomainService inventoryDomainService;

    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;

    private final StockDecrementedMessagePublisher stockDecrementedMessagePublisher;
    private final StockIncrementedMessagePublisher stockIncrementedEventDomainEventPublisher;

    public InventoryHelper(
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
    public StockDecrementedEvent decreaseStock(UUID inventoryId, UUID warehouseId, StockJournalReason stockJournalReason ,int quantity) {
        Optional<Inventory> sourceInventory = inventoryRepository.findByIdAndWarehouseId(inventoryId, warehouseId);
        if (sourceInventory.isEmpty()) {
            throw new IllegalArgumentException("Inventory not found");
        }

        return inventoryDomainService.deductStock(sourceInventory.get(), quantity, stockJournalReason, stockDecrementedMessagePublisher);
    }

    @Transactional
    public StockIncrementedEvent increaseStock(UUID inventoryId, UUID warehouseId, StockJournalReason stockJournalReason, int quantity) {
        Optional<Inventory>  destinationInventory = inventoryRepository.findByIdAndWarehouseId(inventoryId, warehouseId);
        if (destinationInventory.isEmpty()) {
            throw new IllegalArgumentException("Inventory not found");
        }
        return inventoryDomainService.incrementStock(destinationInventory.get(), quantity, stockJournalReason ,stockIncrementedEventDomainEventPublisher);
    }

    public StockTransferEventResult manualTransferStock(UUID inventoryId, TransferInventoryCommand transferInventoryCommand) {
        log.info("Stock transfer is started for inventory id: {}", inventoryId);
        StockDecrementedEvent stockDecrementedEvent = decreaseStock(inventoryId, transferInventoryCommand.getFromWarehouseId(), StockJournalReason.MANUAL_TRANSFER_OUT, transferInventoryCommand.getQuantity());
        StockIncrementedEvent stockIncrementedEvent = increaseStock(inventoryId, transferInventoryCommand.getFromWarehouseId(), StockJournalReason.MANUAL_TRANSFER_IN, transferInventoryCommand.getQuantity());

        persistTransferStock(stockDecrementedEvent.getInventory(), stockIncrementedEvent.getInventory());
        log.info("Stock transfer is completed for inventory id: {}", inventoryId);

        return new StockTransferEventResult(stockDecrementedEvent, stockIncrementedEvent);
    }

    public StockTransferEventResult autoTransferStock(AutoTransferInventoryCommand autoTransferInventoryCommand) {
        // find empty stock warehouse
        Optional<Warehouse> destinationWarehouse = warehouseRepository.findById(autoTransferInventoryCommand.getToWarehouseId());
        if (destinationWarehouse.isEmpty()) {
            throw new IllegalArgumentException("Destination stock warehouse not found");
        }

        // find nearest warehouse
        Optional<UUID> nearestWarehouseId = findNearestWarehouse(autoTransferInventoryCommand.getInventoryId(), destinationWarehouse.get().getId().getValue(), destinationWarehouse.get().getLatitude(), destinationWarehouse.get().getLongitude());

        if (nearestWarehouseId.isEmpty()) {
            throw new IllegalArgumentException("No warehouse found to transfer stock");
        }

        log.info("Auto Stock transfer is started for inventory id: {}", autoTransferInventoryCommand.getInventoryId());

        StockDecrementedEvent stockDecrementedEvent = decreaseStock(autoTransferInventoryCommand.getInventoryId(), nearestWarehouseId.get(), StockJournalReason.AUTO_TRANSFER_OUT, autoTransferInventoryCommand.getQuantity());
        StockIncrementedEvent stockIncrementedEvent = increaseStock(autoTransferInventoryCommand.getInventoryId(), destinationWarehouse.get().getId().getValue(), StockJournalReason.AUTO_TRANSFER_IN, autoTransferInventoryCommand.getQuantity());

        persistTransferStock(stockDecrementedEvent.getInventory(), stockIncrementedEvent.getInventory());
        log.info("Auto Stock transfer is completed for inventory id: {}", autoTransferInventoryCommand.getInventoryId());

        return new StockTransferEventResult(stockDecrementedEvent, stockIncrementedEvent);
    }


    @Transactional
    public void persistTransferStock(Inventory sourceInventory, Inventory destinationInventory) {
        // update quantity of source and destination inventory warehouse. Total quantity should be same before and after transfer
        inventoryRepository.updateQuantityByInventoryIdAndWarehouseId(sourceInventory);
        inventoryRepository.updateQuantityByInventoryIdAndWarehouseId(destinationInventory);
    }

    public Optional<UUID> findNearestWarehouse(UUID inventoryID,UUID destinationWarehouseId, Double destinationLatitude, Double destinationLongitude) {
        List<UUID> warehouseIds = inventoryRepository.findWarehouseIdsByInventoryId(inventoryID, destinationWarehouseId);
        // find nearest warehouse
        return warehouseRepository.findNearestWarehouseWithinIds(destinationLatitude, destinationLongitude, warehouseIds);
    }

}
