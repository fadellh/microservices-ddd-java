package com.mwc.inventory.service.domain.ports.output.message.publisher;

import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.inventory.service.domain.event.StockIncrementedEvent;

public interface StockIncrementedMessagePublisher extends DomainEventPublisher<StockIncrementedEvent> {
}
