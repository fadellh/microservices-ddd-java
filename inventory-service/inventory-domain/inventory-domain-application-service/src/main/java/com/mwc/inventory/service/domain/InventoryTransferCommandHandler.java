package com.mwc.inventory.service.domain;

import com.mwc.inventory.service.domain.dto.transfer.AutoTransferInventoryCommand;
import com.mwc.inventory.service.domain.dto.transfer.StockTransferEventResult;
import com.mwc.inventory.service.domain.dto.transfer.TransferInventoryCommand;
import com.mwc.inventory.service.domain.dto.transfer.TransferInventoryResponse;
import com.mwc.inventory.service.domain.event.StockDecrementedEvent;
import com.mwc.inventory.service.domain.event.StockIncrementedEvent;
import com.mwc.inventory.service.domain.event.StockUpdatedEvent;
import com.mwc.inventory.service.domain.mapper.InventoryDataMapper;
import com.mwc.inventory.service.domain.ports.output.message.publisher.StockDecrementedMessagePublisher;
import com.mwc.inventory.service.domain.ports.output.message.publisher.StockIncrementedMessagePublisher;
import com.mwc.inventory.service.domain.ports.output.message.publisher.StockUpdatedMessagePublisher;
import com.mwc.inventory.service.domain.valueobject.StockJournalReason;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class InventoryTransferCommandHandler {
    private final InventoryHelper inventoryTransferHelper;
    private final InventoryDataMapper inventoryDataMapper;

    private final StockDecrementedMessagePublisher stockDecrementedMessagePublisher;
    private final StockIncrementedMessagePublisher stockIncrementedMessagePublisher;
    private final StockUpdatedMessagePublisher stockUpdatedMessagePublisher;

    public InventoryTransferCommandHandler(
            InventoryHelper inventoryTransferHelper,
            InventoryDataMapper inventoryDataMapper,
            StockDecrementedMessagePublisher stockDecrementedMessagePublisher,
            StockIncrementedMessagePublisher stockIncrementedMessagePublisher, StockUpdatedMessagePublisher stockUpdatedMessagePublisher
    ) {
        this.inventoryTransferHelper = inventoryTransferHelper;
        this.inventoryDataMapper = inventoryDataMapper;
        this.stockDecrementedMessagePublisher = stockDecrementedMessagePublisher;
        this.stockIncrementedMessagePublisher = stockIncrementedMessagePublisher;
        this.stockUpdatedMessagePublisher = stockUpdatedMessagePublisher;
    }

    @Transactional
    public TransferInventoryResponse transferInventory(UUID inventoryId, TransferInventoryCommand transferInventoryCommand) {
        StockTransferEventResult stockTransferEventResult = inventoryTransferHelper.manualTransferStock(inventoryId, transferInventoryCommand);

        StockUpdatedEvent stockDecrementedEvent = stockTransferEventResult.getStockDecrementedEvent();
        StockUpdatedEvent stockIncrementedEvent = stockTransferEventResult.getStockIncrementedEvent();

        stockUpdatedMessagePublisher.publish(stockDecrementedEvent);
        stockUpdatedMessagePublisher.publish(stockIncrementedEvent);

        return inventoryDataMapper.inventoryToTransferInventoryResponse(stockDecrementedEvent.getInventory(), stockIncrementedEvent.getInventory());
    }

    @Transactional
    public TransferInventoryResponse autoTransferInventory(AutoTransferInventoryCommand autoTransferInventoryCommand) {

        // transfer stock to nearest warehouse
        StockTransferEventResult stockTransferEventResult = inventoryTransferHelper.autoTransferStock(autoTransferInventoryCommand);

        StockUpdatedEvent stockDecrementedEvent = stockTransferEventResult.getStockDecrementedEvent();
        StockUpdatedEvent stockIncrementedEvent = stockTransferEventResult.getStockIncrementedEvent();

        stockUpdatedMessagePublisher.publish(stockDecrementedEvent);
        stockUpdatedMessagePublisher.publish(stockIncrementedEvent);

        return inventoryDataMapper.inventoryToTransferInventoryResponse(stockDecrementedEvent.getInventory(), stockIncrementedEvent.getInventory());
    }

}
