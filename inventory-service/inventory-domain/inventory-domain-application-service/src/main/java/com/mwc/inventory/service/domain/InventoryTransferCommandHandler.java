package com.mwc.inventory.service.domain;

import com.mwc.inventory.service.domain.dto.transfer.AutoTransferInventoryCommand;
import com.mwc.inventory.service.domain.dto.transfer.StockTransferEventResult;
import com.mwc.inventory.service.domain.dto.transfer.TransferInventoryCommand;
import com.mwc.inventory.service.domain.dto.transfer.TransferInventoryResponse;
import com.mwc.inventory.service.domain.event.StockDecrementedEvent;
import com.mwc.inventory.service.domain.event.StockIncrementedEvent;
import com.mwc.inventory.service.domain.mapper.InventoryDataMapper;
import com.mwc.inventory.service.domain.ports.output.message.publisher.StockDecrementedMessagePublisher;
import com.mwc.inventory.service.domain.ports.output.message.publisher.StockIncrementedMessagePublisher;
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

    public InventoryTransferCommandHandler(
            InventoryHelper inventoryTransferHelper,
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
    public TransferInventoryResponse transferInventory(UUID inventoryId, TransferInventoryCommand transferInventoryCommand) {
        StockTransferEventResult stockTransferEventResult = inventoryTransferHelper.manualTransferStock(inventoryId, transferInventoryCommand);

        StockDecrementedEvent stockDecrementedEvent = stockTransferEventResult.getStockDecrementedEvent();
        StockIncrementedEvent stockIncrementedEvent = stockTransferEventResult.getStockIncrementedEvent();

        stockDecrementedMessagePublisher.publish(stockDecrementedEvent);
        stockIncrementedMessagePublisher.publish(stockIncrementedEvent);

        return inventoryDataMapper.inventoryToTransferInventoryResponse(stockDecrementedEvent.getInventory(), stockIncrementedEvent.getInventory());
    }

    @Transactional
    public TransferInventoryResponse autoTransferInventory(AutoTransferInventoryCommand autoTransferInventoryCommand) {

        // transfer stock to nearest warehouse
        StockTransferEventResult stockTransferEventResult = inventoryTransferHelper.autoTransferStock(autoTransferInventoryCommand);

        StockDecrementedEvent stockDecrementedEvent = stockTransferEventResult.getStockDecrementedEvent();
        StockIncrementedEvent stockIncrementedEvent = stockTransferEventResult.getStockIncrementedEvent();

        stockDecrementedMessagePublisher.publish(stockDecrementedEvent);
        stockIncrementedMessagePublisher.publish(stockIncrementedEvent);

        return inventoryDataMapper.inventoryToTransferInventoryResponse(stockDecrementedEvent.getInventory(), stockIncrementedEvent.getInventory());
    }

}
