package com.mwc.order.service.domain.event;

import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.order.service.domain.entity.Order;

import java.time.ZonedDateTime;

public class OrderStatusUpdatedEvent extends OrderEvent {
    private final DomainEventPublisher<OrderStatusUpdatedEvent> orderStatusUpdatedEventDomainEventPublisher;

    public OrderStatusUpdatedEvent(Order order, ZonedDateTime createdAt, DomainEventPublisher<OrderStatusUpdatedEvent> orderStatusUpdatedEventDomainEventPublisher) {
        super(order, createdAt);
        this.orderStatusUpdatedEventDomainEventPublisher = orderStatusUpdatedEventDomainEventPublisher;
    }

    @Override
    public Order getEntity() {
        return null;
    }

    @Override
    public void fire() {
        orderStatusUpdatedEventDomainEventPublisher.publish(this);
    }
}
