package com.mwc.inventory.service.domain;

import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.inventory.service.domain.entity.Inventory;
import com.mwc.inventory.service.domain.event.*;
import com.mwc.inventory.service.domain.valueobject.StockJournalReason;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryDomainService {
    StockUpdatedEvent deductStock(Inventory inventory, int quantity,StockJournalReason stockJournalReason, DomainEventPublisher<StockUpdatedEvent> stockUpdatedEventDomainEventPublisher);

    StockUpdatedEvent incrementStock(Inventory inventory, int quantity, StockJournalReason stockJournalReason, DomainEventPublisher<StockUpdatedEvent> stockUpdatedEventDomainEventPublisher);
    // order stock
    InventoryEvent orderStock(Inventory inventory, int quantity, DomainEventPublisher<OrderStockCompletedEvent> orderStockCompletedEventDomainEventPublisher, DomainEventPublisher<OrderStockFailedEvent> orderStockFailedEventDomainEventPublisher);

//    StockDeductedEvent transferStock(Inventory sourceInventory, Inventory destinationInventory, int quantityTransfer, DomainEventPublisher<StockDeductedEvent> stockDeductedEventDomainEventPublisher);
}
