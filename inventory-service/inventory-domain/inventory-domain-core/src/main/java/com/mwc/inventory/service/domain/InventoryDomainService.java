package com.mwc.inventory.service.domain;

import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.inventory.service.domain.entity.Inventory;
import com.mwc.inventory.service.domain.event.StockUpdatedEvent;

public interface InventoryDomainService {
    StockUpdatedEvent updateStock(Inventory inventory, int quantity, DomainEventPublisher<StockUpdatedEvent> stockUpdatedEventDomainEventPublisher);

}
