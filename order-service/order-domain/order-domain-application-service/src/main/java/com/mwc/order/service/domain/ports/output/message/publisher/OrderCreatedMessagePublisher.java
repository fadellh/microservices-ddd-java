package com.mwc.order.service.domain.ports.output.message.publisher;

import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.order.service.domain.event.OrderCreatedEvent;

public interface OrderCreatedMessagePublisher extends DomainEventPublisher<OrderCreatedEvent> {
}
