package com.mwc.inventory.service.domain;

import com.mwc.inventory.service.domain.dto.transfer.TransferInventoryCommand;
import com.mwc.inventory.service.domain.dto.transfer.TransferInventoryResponse;
import com.mwc.inventory.service.domain.event.StockDecrementedEvent;
import com.mwc.inventory.service.domain.event.StockIncrementedEvent;
import com.mwc.inventory.service.domain.mapper.InventoryDataMapper;
import com.mwc.inventory.service.domain.ports.output.message.publisher.StockDecrementedMessagePublisher;
import com.mwc.inventory.service.domain.ports.output.message.publisher.StockIncrementedMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
public class InventoryTransferCommandHandler {
    private final InventoryTransferHelper inventoryTransferHelper;
    private final InventoryDataMapper inventoryDataMapper;

    private final StockDecrementedMessagePublisher stockDecrementedMessagePublisher;
    private final StockIncrementedMessagePublisher stockIncrementedMessagePublisher;

    public InventoryTransferCommandHandler(
            InventoryTransferHelper inventoryTransferHelper,
            InventoryDataMapper inventoryDataMapper,
            StockDecrementedMessagePublisher stockDecrementedMessagePublisher,
            StockIncrementedMessagePublisher stockIncrementedMessagePublisher
    ) {
        this.inventoryTransferHelper = inventoryTransferHelper;
        this.inventoryDataMapper = inventoryDataMapper;
        this.stockDecrementedMessagePublisher = stockDecrementedMessagePublisher;
        this.stockIncrementedMessagePublisher = stockIncrementedMessagePublisher;
    }

    @Transactional
    public TransferInventoryResponse manualTransferInventory(UUID inventoryId, TransferInventoryCommand transferInventoryCommand) {
        log.info("Stock transfer is started for inventory id: {}", inventoryId);
        StockDecrementedEvent stockDecrementedEvent =  inventoryTransferHelper.decreaseStock(inventoryId, transferInventoryCommand.getFromWarehouseId(), transferInventoryCommand.getQuantity());
        StockIncrementedEvent stockIncrementedEvent = inventoryTransferHelper.increaseStock(inventoryId, transferInventoryCommand.getToWarehouseId(), transferInventoryCommand.getQuantity());

        inventoryTransferHelper.persistTransferStock(stockDecrementedEvent.getInventory(), stockIncrementedEvent.getInventory());
        log.info("Stock transfer is completed for inventory id: {}", inventoryId);

        stockDecrementedMessagePublisher.publish(stockDecrementedEvent);
        stockIncrementedMessagePublisher.publish(stockIncrementedEvent);

        return inventoryDataMapper.inventoryToTransferInventoryResponse(stockDecrementedEvent.getInventory(), stockIncrementedEvent.getInventory());
    }

}
