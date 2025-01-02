package com.mwc.inventory.service.domain;

import com.mwc.inventory.service.domain.dto.message.StockDecrementRequest;
import com.mwc.inventory.service.domain.dto.transfer.AutoTransferInventoryCommand;
import com.mwc.inventory.service.domain.dto.transfer.StockTransferEventResult;
import com.mwc.inventory.service.domain.event.InventoryEvent;
import com.mwc.inventory.service.domain.event.StockDecrementedEvent;
import com.mwc.inventory.service.domain.event.StockIncrementedEvent;
import com.mwc.inventory.service.domain.event.StockUpdatedEvent;
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
        InventoryEvent inventoryEvent = inventoryHelper.orderStock(stockDecrementRequest.getInventoryId(), stockDecrementRequest.getWarehouseId(), stockDecrementRequest.getOrderId() ,stockDecrementRequest.getQuantity());
        fireEvent(inventoryEvent);
    }

    private void incrementFireEvent(StockIncrementedEvent stockIncrementedEvent) {
        log.info("Publishing stock increment event with inventory id: {} and warehouse id: {} and product id: {}",
                stockIncrementedEvent.getInventory().getId().getValue(),
                stockIncrementedEvent.getInventory().getWarehouseId().getValue(),
                stockIncrementedEvent.getInventory().getProductId().getValue());
        stockIncrementedEvent.fire();
    }

    private void fireEvent(InventoryEvent inventoryEvent) {
        log.info("Publishing order stock event with inventory id: {} and warehouse id: {} and product id: {}",
                inventoryEvent.getInventory().getId().getValue(),
                inventoryEvent.getInventory().getWarehouseId().getValue(),
                inventoryEvent.getInventory().getProductId().getValue());
        inventoryEvent.fire();
    }
}
