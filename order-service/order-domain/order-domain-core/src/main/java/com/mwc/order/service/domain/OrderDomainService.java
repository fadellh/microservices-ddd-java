package com.mwc.order.service.domain;

import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.order.service.domain.entity.Order;
import com.mwc.order.service.domain.event.OrderCreatedEvent;

public interface OrderDomainService {
    OrderCreatedEvent validateAndInitiateOrder(Order order,
                                               DomainEventPublisher<OrderCreatedEvent> orderCreatedEventDomainEventPublisher);
}
