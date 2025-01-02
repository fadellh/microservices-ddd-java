package com.mwc.inventory.service.domain.event;

import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.inventory.service.domain.entity.Inventory;

import java.time.ZonedDateTime;

public class OrderStockFailedEvent extends InventoryEvent {
    private final DomainEventPublisher<OrderStockFailedEvent> stockDecrementFailedEventDomainEventPublisher;

    public OrderStockFailedEvent(Inventory inventory, ZonedDateTime createdAt, DomainEventPublisher<OrderStockFailedEvent> stockDecrementFailedEventDomainEventPublisher) {
        super(inventory, createdAt);
        this.stockDecrementFailedEventDomainEventPublisher = stockDecrementFailedEventDomainEventPublisher;
    }

    @Override
    public Inventory getEntity() {
        return getInventory();
    }

    @Override
    public final void fire() {
        stockDecrementFailedEventDomainEventPublisher.publish(this);
    }
}
