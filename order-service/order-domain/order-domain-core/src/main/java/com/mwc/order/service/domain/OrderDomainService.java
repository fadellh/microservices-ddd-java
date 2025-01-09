package com.mwc.order.service.domain;

import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.domain.valueobject.OrderStatus;
import com.mwc.order.service.domain.entity.Admin;
import com.mwc.order.service.domain.entity.Order;
import com.mwc.order.service.domain.entity.OrderItem;
import com.mwc.order.service.domain.event.OrderApproveFailedEvent;
import com.mwc.order.service.domain.event.OrderApprovedEvent;
import com.mwc.order.service.domain.event.OrderCreatedEvent;

import java.util.List;

public interface OrderDomainService {
    OrderCreatedEvent validateAndInitiateOrder(Order order,
                                               List<OrderItem> orderItems ,
                                               DomainEventPublisher<OrderCreatedEvent> orderCreatedEventDomainEventPublisher);

    OrderApprovedEvent initApproveOrder(Order order, Admin admin,
                                        DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher);

    void approveOrder(Order order);

    void initReviewPayment(Order order);

    OrderApproveFailedEvent cancelApproveOrder(Order order, List<String> failureMessages, DomainEventPublisher<OrderApproveFailedEvent> orderApproveFailedEventDomainEventPublisher);

}
