package com.mwc.inventory.service.domain.ports.input.message.listener;


import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.inventory.service.domain.dto.message.StockDecrementRequest;
import com.mwc.inventory.service.domain.event.StockDecrementedEvent;

public interface StockDecrementRequestMessageListener  {
    void decrementStock(StockDecrementRequest stockDecrementRequest);
}
