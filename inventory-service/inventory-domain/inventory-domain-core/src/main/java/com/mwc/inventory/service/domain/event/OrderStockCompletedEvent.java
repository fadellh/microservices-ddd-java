package com.mwc.inventory.service.domain.event;


import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.inventory.service.domain.entity.Inventory;

import java.time.ZonedDateTime;

public class OrderStockCompletedEvent extends InventoryEvent {
    private final DomainEventPublisher<OrderStockCompletedEvent> stockDecrementCompletedEventDomainEventPublisher;

    public OrderStockCompletedEvent(Inventory inventory,
                                    ZonedDateTime createdAt,
                                    DomainEventPublisher<OrderStockCompletedEvent> stockDecrementCompletedEventDomainEventPublisher) {
        super(inventory, createdAt);
        this.stockDecrementCompletedEventDomainEventPublisher = stockDecrementCompletedEventDomainEventPublisher;
    }

    @Override
    public Inventory getEntity() {
        return getInventory();
    }

    @Override
    public void fire() {
        stockDecrementCompletedEventDomainEventPublisher.publish(this);
    }
}
