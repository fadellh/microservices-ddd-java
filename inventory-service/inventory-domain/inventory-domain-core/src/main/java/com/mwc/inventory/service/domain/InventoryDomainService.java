package com.mwc.inventory.service.domain;

import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.inventory.service.domain.entity.Inventory;
import com.mwc.inventory.service.domain.event.StockDecrementedEvent;
import com.mwc.inventory.service.domain.event.StockIncrementedEvent;
import com.mwc.inventory.service.domain.valueobject.StockJournalReason;

public interface InventoryDomainService {
    StockDecrementedEvent deductStock(Inventory inventory, int quantity,StockJournalReason stockJournalReason, DomainEventPublisher<StockDecrementedEvent> stockDeductedEventDomainEventPublisher);

    StockIncrementedEvent incrementStock(Inventory inventory, int quantity, StockJournalReason stockJournalReason, DomainEventPublisher<StockIncrementedEvent> stockIncrementedEventDomainEventPublisher);

//    StockDeductedEvent transferStock(Inventory sourceInventory, Inventory destinationInventory, int quantityTransfer, DomainEventPublisher<StockDeductedEvent> stockDeductedEventDomainEventPublisher);
}
