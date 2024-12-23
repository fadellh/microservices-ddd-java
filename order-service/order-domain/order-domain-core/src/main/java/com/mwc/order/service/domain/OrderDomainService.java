package com.mwc.order.service.domain;

import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.order.service.domain.entity.Order;
import com.mwc.order.service.domain.entity.OrderItem;
import com.mwc.order.service.domain.event.OrderCreatedEvent;

import java.util.List;

public interface OrderDomainService {
    OrderCreatedEvent validateAndInitiateOrder(Order order,
                                               List<OrderItem> orderItems ,
                                               DomainEventPublisher<OrderCreatedEvent> orderCreatedEventDomainEventPublisher);
}
