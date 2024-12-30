//package com.mwc.inventory.service.domain.event;
//
//import com.mwc.domain.event.publisher.DomainEventPublisher;
//import com.mwc.inventory.service.domain.entity.Inventory;
//
//import java.time.ZonedDateTime;
//
//public class StockTransferredEvent extends InventoryEvent {
//    private final DomainEventPublisher<StockTransferredEvent> stockTransferedEventDomainEventPublisher;
//
//    public StockTransferredEvent(Inventory sourceInventory,
//                                 Inventory destinationInventory,
//                                 ZonedDateTime createdAt,
//                                 DomainEventPublisher<StockTransferredEvent> stockTransferedEventDomainEventPublisher) {
//        super(sourceInventory, destinationInventory, createdAt);
//        this.stockTransferedEventDomainEventPublisher = stockTransferedEventDomainEventPublisher;
//    }
//
//    @Override
//    public Inventory getEntity() {
//        return getInventory();
//    }
//
//    @Override
//    public void fire() {
//        stockTransferedEventDomainEventPublisher.publish(this);
//    }
//}
