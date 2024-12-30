package com.mwc.inventory.service.domain;

import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.inventory.service.domain.entity.Inventory;
import com.mwc.inventory.service.domain.event.StockDeductedEvent;
import com.mwc.inventory.service.domain.event.StockUpdatedEvent;

public interface InventoryDomainService {
    StockDeductedEvent deductStock(Inventory inventory, int quantity, DomainEventPublisher<StockDeductedEvent> stockDeductedEventDomainEventPublisher);
    StockDeductedEvent transferStock(Inventory inventory, int quantity, DomainEventPublisher<StockDeductedEvent> stockDeductedEventDomainEventPublisher);
}
