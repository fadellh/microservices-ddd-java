package com.mwc.order.service.domain.ports.output.message.publisher;

import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.order.service.domain.event.OrderApprovedEvent;

public interface OrderApprovedDecrementStockRequestMessagePublisher extends DomainEventPublisher<OrderApprovedEvent> {

    void publish(OrderApprovedEvent domainEvent);
}
