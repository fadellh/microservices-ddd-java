package com.mwc.inventory.service.domain.ports.input.message.listener.order;


import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.inventory.service.domain.event.StockDeductedEvent;

public interface StockDeductRequestMessageListener extends DomainEventPublisher<StockDeductedEvent> {
}
