package com.mwc.inventory.service.domain;

import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.inventory.service.domain.entity.Inventory;
import com.mwc.inventory.service.domain.event.StockUpdatedEvent;
import com.mwc.inventory.service.domain.exception.NegativeQuantityException;

import java.time.ZonedDateTime;

public class InventoryDomainServiceImpl implements InventoryDomainService {

    @Override
    public StockUpdatedEvent updateStock(Inventory inventory, int quantity, DomainEventPublisher<StockUpdatedEvent> stockUpdatedEventDomainEventPublisher) {
        inventory.setQuantity(quantity);
        return new StockUpdatedEvent(inventory, ZonedDateTime.now(), stockUpdatedEventDomainEventPublisher);
    }
}