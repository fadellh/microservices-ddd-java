package com.mwc.order.service.domain.event;

import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.order.service.domain.entity.Order;

import java.time.ZonedDateTime;

public class OrderApproveFailedEvent extends OrderEvent {
    DomainEventPublisher<OrderApproveFailedEvent> orderApproveFailedEventDomainEventPublisher;

    public OrderApproveFailedEvent(Order order, ZonedDateTime createdAt, DomainEventPublisher<OrderApproveFailedEvent> orderApproveFailedEventDomainEventPublisher) {
        super(order, createdAt);
        this.orderApproveFailedEventDomainEventPublisher = orderApproveFailedEventDomainEventPublisher;
    }

    @Override
    public Order getEntity() {
        return null;
    }

    @Override
    public void fire() {
        orderApproveFailedEventDomainEventPublisher.publish(this);
    }
}
