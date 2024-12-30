package com.mwc.inventory.service.domain.event;

import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.inventory.service.domain.entity.Inventory;

import java.time.ZonedDateTime;

public class StockIncrementedEvent extends InventoryEvent {
    private final DomainEventPublisher<StockIncrementedEvent> stockIncrementedEventDomainEventPublisher;

    public StockIncrementedEvent(Inventory inventory,
                                 ZonedDateTime createdAt,
                                 DomainEventPublisher<StockIncrementedEvent> stockIncrementedEventDomainEventPublisher) {
        super(inventory, createdAt);
        this.stockIncrementedEventDomainEventPublisher = stockIncrementedEventDomainEventPublisher;
    }

    @Override
    public Inventory getEntity() {
        return getInventory();
    }

    @Override
    public void fire() {
        stockIncrementedEventDomainEventPublisher.publish(this);
    }
}
