package com.mwc.inventory.service.domain.ports.output.message.publisher;

import com.mwc.inventory.service.domain.event.StockUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NoStockUpdatedMessagePublisherMessagePublisher implements StockUpdatedMessagePublisher {
    @Override
    public void publish(StockUpdatedEvent domainEvent) {

    }
}
