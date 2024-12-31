package com.mwc.inventory.service.domain;

import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.inventory.service.domain.entity.Inventory;
import com.mwc.inventory.service.domain.event.StockDecrementedEvent;
import com.mwc.inventory.service.domain.event.StockIncrementedEvent;
import com.mwc.inventory.service.domain.valueobject.Quantity;
import com.mwc.inventory.service.domain.valueobject.StockJournalReason;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;

@Slf4j
public class InventoryDomainServiceImpl implements InventoryDomainService {

    @Override
    public StockDecrementedEvent deductStock(Inventory inventory, int quantity,StockJournalReason stockJournalReason, DomainEventPublisher<StockDecrementedEvent> stockDeductedEventDomainEventPublisher) {
        inventory.checkIfEnoughQuantity(new Quantity(quantity));

        inventory.reduceStock(new Quantity(quantity), stockJournalReason);
        return new StockDecrementedEvent(inventory, ZonedDateTime.now(), stockDeductedEventDomainEventPublisher);
    }

    @Override
    public StockIncrementedEvent incrementStock(Inventory inventory, int quantity,StockJournalReason stockJournalReason, DomainEventPublisher<StockIncrementedEvent> stockIncrementedEventDomainEventPublisher) {
        inventory.addStock(new Quantity(quantity), stockJournalReason);
        return new StockIncrementedEvent(inventory, ZonedDateTime.now(), stockIncrementedEventDomainEventPublisher);
    }

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