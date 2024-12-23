package com.mwc.inventory.service.domain.event;

import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.inventory.service.domain.entity.Inventory;

import java.time.ZonedDateTime;

public class StockUpdatedEvent extends InventoryEvent {
    private final DomainEventPublisher<StockUpdatedEvent> stockUpdatedEventDomainEventPublisher;

    public StockUpdatedEvent(Inventory inventory, ZonedDateTime createdAt, DomainEventPublisher<StockUpdatedEvent> stockUpdatedEventDomainEventPublisher) {
        super(inventory, createdAt);
        this.stockUpdatedEventDomainEventPublisher = stockUpdatedEventDomainEventPublisher;
    }

    @Override
    public Inventory getEntity() {
        return getInventory();
    }

    @Override
    public void fire() {
        stockUpdatedEventDomainEventPublisher.publish(this);
    }
}