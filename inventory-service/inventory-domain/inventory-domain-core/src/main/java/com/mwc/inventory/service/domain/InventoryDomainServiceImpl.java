package com.mwc.inventory.service.domain;

import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.domain.valueobject.OrderId;
import com.mwc.inventory.service.domain.entity.Inventory;
import com.mwc.inventory.service.domain.event.*;
import com.mwc.inventory.service.domain.valueobject.Quantity;
import com.mwc.inventory.service.domain.valueobject.StockJournalReason;
import com.mwc.inventory.service.domain.valueobject.StockJournalStatus;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class InventoryDomainServiceImpl implements InventoryDomainService {

    @Override
    public StockUpdatedEvent deductStock(Inventory inventory, int quantity,StockJournalReason stockJournalReason, DomainEventPublisher<StockUpdatedEvent> stockUpdatedEventDomainEventPublisher) {
        inventory.checkIfEnoughQuantity(new Quantity(quantity));

        inventory.reduceStock(new Quantity(quantity), stockJournalReason, StockJournalStatus.REQUEST);
        return new StockUpdatedEvent(inventory, ZonedDateTime.now(), stockUpdatedEventDomainEventPublisher);
    }

    @Override
    public StockUpdatedEvent incrementStock(Inventory inventory, int quantity,StockJournalReason stockJournalReason, DomainEventPublisher<StockUpdatedEvent> stockUpdatedEventDomainEventPublisher) {
        inventory.addStock(new Quantity(quantity), stockJournalReason, StockJournalStatus.APPROVED);
        return new StockUpdatedEvent(inventory, ZonedDateTime.now(), stockUpdatedEventDomainEventPublisher);
    }

    @Override
    public InventoryEvent orderStock(Inventory inventory, int quantity, DomainEventPublisher<OrderStockCompletedEvent> orderStockCompletedEventDomainEventPublisher, DomainEventPublisher<OrderStockFailedEvent> orderStockFailedEventDomainEventPublisher) {
        inventory.order(new Quantity(quantity));

        if (inventory.getFailureMessages().isEmpty()){
            log.info("Stock is ordered for inventory id: {}", inventory.getId().getValue());
            return new OrderStockCompletedEvent(inventory, ZonedDateTime.now(), orderStockCompletedEventDomainEventPublisher);
        } else {
            log.info("Stock order is failed for inventory id: {}", inventory.getId().getValue());
            return new OrderStockFailedEvent(inventory, ZonedDateTime.now(), orderStockFailedEventDomainEventPublisher);
        }
    }

    // order stock


//    @Override
//    public StockDecrementedEvent transferStock(Inventory sourceInventory, Inventory destinationInventory, int quantityTransfer, DomainEventPublisher<StockDecrementedEvent> stockDeductedEventDomainEventPublisher) {
//        // check if sourceInventory has enough quantity to transfer -> if not throw new NotEnoughInventoryException
//        sourceInventory.checkIfEnoughQuantity(new Quantity(quantityTransfer));
//
//        // Transfer stock from sourceInventory to destinationInventory
//        sourceInventory.reduceStock(new Quantity(quantityTransfer), StockJournalReason.MANUAL_TRANSFER_OUT);
//        destinationInventory.addStock(new Quantity(quantityTransfer), StockJournalReason.MANUAL_TRANSFER_IN);
//
//
//        return new StockDecrementedEvent(inventory, ZonedDateTime.now(), stockDeductedEventDomainEventPublisher);
//    }

}