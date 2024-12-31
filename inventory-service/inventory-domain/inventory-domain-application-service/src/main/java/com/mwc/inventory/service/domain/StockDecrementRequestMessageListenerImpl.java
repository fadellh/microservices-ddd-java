package com.mwc.inventory.service.domain;

import com.mwc.inventory.service.domain.dto.message.StockDecrementRequest;
import com.mwc.inventory.service.domain.dto.transfer.AutoTransferInventoryCommand;
import com.mwc.inventory.service.domain.dto.transfer.StockTransferEventResult;
import com.mwc.inventory.service.domain.event.StockDecrementedEvent;
import com.mwc.inventory.service.domain.event.StockIncrementedEvent;
import com.mwc.inventory.service.domain.ports.input.message.listener.StockDecrementRequestMessageListener;
import com.mwc.inventory.service.domain.valueobject.StockJournalReason;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StockDecrementRequestMessageListenerImpl implements StockDecrementRequestMessageListener {

    private final InventoryHelper inventoryHelper;

    public StockDecrementRequestMessageListenerImpl(InventoryHelper inventoryHelper) {
        this.inventoryHelper = inventoryHelper;
    }

    @Override
    public void decrementStock(StockDecrementRequest stockDecrementRequest) {
        try {
            StockDecrementedEvent stockDecrementedEvent = inventoryHelper.decreaseStock(stockDecrementRequest.getInventoryId(), stockDecrementRequest.getWarehouseId(), StockJournalReason.ORDER ,stockDecrementRequest.getQuantity());
            fireEvent(stockDecrementedEvent);
        } catch (Exception e) {
            log.warn("Error processing stock decrement request for inventory id: {} and warehouse id: {} and product id: {}",
                    stockDecrementRequest.getInventoryId(),
                    stockDecrementRequest.getWarehouseId(),
                    stockDecrementRequest.getProductId(),
                    e);

            // Try to auto transfer stock
            StockTransferEventResult stockTransferEventResult = inventoryHelper.autoTransferStock(
                   AutoTransferInventoryCommand.builder()
                           .inventoryId(stockDecrementRequest.getInventoryId())
                           .toWarehouseId(stockDecrementRequest.getWarehouseId())
                           .quantity(stockDecrementRequest.getQuantity())
                           .build()
            );

            StockDecrementedEvent stockDecrementedEvent = stockTransferEventResult.getStockDecrementedEvent();
            StockIncrementedEvent incrementFireEvent = stockTransferEventResult.getStockIncrementedEvent();

            fireEvent(stockDecrementedEvent);
            incrementFireEvent(incrementFireEvent);
        }

        // Try order again
        StockDecrementedEvent stockDecrementedEvent = inventoryHelper.decreaseStock(stockDecrementRequest.getInventoryId(), stockDecrementRequest.getWarehouseId(), StockJournalReason.ORDER ,stockDecrementRequest.getQuantity());
        fireEvent(stockDecrementedEvent);

    }

    private void incrementFireEvent(StockIncrementedEvent stockIncrementedEvent) {
        log.info("Publishing stock increment event with inventory id: {} and warehouse id: {} and product id: {}",
                stockIncrementedEvent.getInventory().getId().getValue(),
                stockIncrementedEvent.getInventory().getWarehouseId().getValue(),
                stockIncrementedEvent.getInventory().getProductId().getValue());
        stockIncrementedEvent.fire();
    }

    private void fireEvent(StockDecrementedEvent stockDecrementedEvent) {
        log.info("Publishing stock decremented event with inventory id: {} and warehouse id: {} and product id: {}",
                stockDecrementedEvent.getInventory().getId().getValue(),
                stockDecrementedEvent.getInventory().getWarehouseId().getValue(),
                stockDecrementedEvent.getInventory().getProductId().getValue());
        stockDecrementedEvent.fire();
    }

}
