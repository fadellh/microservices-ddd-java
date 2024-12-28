package com.mwc.order.service.domain.event;

import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.order.service.domain.entity.Order;

import java.time.ZonedDateTime;


public class OrderApprovedEvent extends OrderEvent {
    private final DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher;

    public OrderApprovedEvent(Order order,
                                ZonedDateTime createdAt,
                              DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher) {
        super(order, createdAt);
        this.orderApprovedEventDomainEventPublisher = orderApprovedEventDomainEventPublisher;
    }


    @Override
    public Order getEntity() {
        return null;
    }

    @Override
    public void fire() {
        orderApprovedEventDomainEventPublisher.publish(this);
    }
}
