package com.mwc.inventory.service.domain;

import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.inventory.service.domain.entity.Inventory;
import com.mwc.inventory.service.domain.event.StockDeductedEvent;
import com.mwc.inventory.service.domain.event.StockUpdatedEvent;
import com.mwc.inventory.service.domain.valueobject.Quantity;
import com.mwc.inventory.service.domain.valueobject.StockJournalReason;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;

@Slf4j
public class InventoryDomainServiceImpl implements InventoryDomainService {

    @Override
    public StockDeductedEvent deductStock(Inventory inventory, int quantity, DomainEventPublisher<StockDeductedEvent> stockDeductedEventDomainEventPublisher) {
        inventory.reduceStock(new Quantity(quantity), StockJournalReason.ORDER);
        return new StockDeductedEvent(inventory, ZonedDateTime.now(), stockDeductedEventDomainEventPublisher);
    }

    @Override
    public StockDeductedEvent transferStock(Inventory inventory, int quantity, DomainEventPublisher<StockDeductedEvent> stockDeductedEventDomainEventPublisher) {
        inventory.reduceStock(new Quantity(quantity), StockJournalReason.MANUAL_TRANSFER_OUT);
        return new StockDeductedEvent(inventory, ZonedDateTime.now(), stockDeductedEventDomainEventPublisher);
    }

}