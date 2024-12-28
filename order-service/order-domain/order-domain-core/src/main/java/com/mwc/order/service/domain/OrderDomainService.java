package com.mwc.order.service.domain;

import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.domain.valueobject.OrderStatus;
import com.mwc.order.service.domain.entity.Admin;
import com.mwc.order.service.domain.entity.Order;
import com.mwc.order.service.domain.entity.OrderItem;
import com.mwc.order.service.domain.event.OrderApprovedEvent;
import com.mwc.order.service.domain.event.OrderCreatedEvent;
import com.mwc.order.service.domain.event.OrderStatusUpdatedEvent;

import java.util.List;

public interface OrderDomainService {
    OrderCreatedEvent validateAndInitiateOrder(Order order,
                                               List<OrderItem> orderItems ,
                                               DomainEventPublisher<OrderCreatedEvent> orderCreatedEventDomainEventPublisher);

    OrderApprovedEvent approveOrder(Order order, OrderStatus orderStatus, Admin admin,
                                    DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher);

}
