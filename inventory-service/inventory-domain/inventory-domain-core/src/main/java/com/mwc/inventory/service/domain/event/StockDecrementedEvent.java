package com.mwc.inventory.service.domain.event;

import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.inventory.service.domain.entity.Inventory;

import java.time.ZonedDateTime;

public class StockDecrementedEvent extends InventoryEvent {
    private final DomainEventPublisher<StockDecrementedEvent> stockDeductedEventDomainEventPublisher;

    public StockDecrementedEvent(Inventory inventory,
                                 ZonedDateTime createdAt,
                                 DomainEventPublisher<StockDecrementedEvent> stockDeductedEventDomainEventPublisher) {
        super(inventory, createdAt);
        this.stockDeductedEventDomainEventPublisher = stockDeductedEventDomainEventPublisher;
    }

    @Override
    public Inventory getEntity() {
        return getInventory();
    }

    @Override
    public void fire() {
        stockDeductedEventDomainEventPublisher.publish(this);
    }
}
