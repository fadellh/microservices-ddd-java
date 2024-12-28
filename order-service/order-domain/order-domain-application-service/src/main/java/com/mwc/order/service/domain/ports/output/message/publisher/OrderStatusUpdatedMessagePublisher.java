package com.mwc.order.service.domain.ports.output.message.publisher;

import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.order.service.domain.event.OrderStatusUpdatedEvent;

public interface OrderStatusUpdatedMessagePublisher extends DomainEventPublisher<OrderStatusUpdatedEvent> {
}
