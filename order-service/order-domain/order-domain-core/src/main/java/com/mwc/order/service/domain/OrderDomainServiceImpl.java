package com.mwc.order.service.domain;

import com.mwc.domain.event.publisher.DomainEventPublisher;
import com.mwc.domain.valueobject.Money;
import com.mwc.domain.valueobject.OrderStatus;
import com.mwc.order.service.domain.entity.Admin;
import com.mwc.order.service.domain.entity.Order;
import com.mwc.order.service.domain.entity.OrderItem;
import com.mwc.order.service.domain.event.OrderApprovedEvent;
import com.mwc.order.service.domain.event.OrderCreatedEvent;
import com.mwc.order.service.domain.event.OrderStatusUpdatedEvent;
import com.mwc.order.service.domain.exception.OrderDomainException;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static com.mwc.domain.DomainConstants.UTC;

@Slf4j
public class OrderDomainServiceImpl implements OrderDomainService {


    @Override
    public OrderCreatedEvent validateAndInitiateOrder(Order order, List<OrderItem> orderItems , DomainEventPublisher<OrderCreatedEvent> orderCreatedEventDomainEventPublisher) {
        order.initiateOrder();
        order.setItems(orderItems);

        BigDecimal totalAmount = order.calculateItemsTotalAmount();
        order.setPrice(new Money(totalAmount));

        order.validateOrder();

        log.info("Order with id: {} is initiated", order.getId().getValue());
        return new OrderCreatedEvent(order, ZonedDateTime.now(ZoneId.of(UTC)), orderCreatedEventDomainEventPublisher);
    }

    @Override
    public OrderApprovedEvent approveOrder(Order order, OrderStatus orderStatus, Admin admin, DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher) {
        validateAdmin(admin);
        order.approve();
        log.info("Order with id: {} is approved", order.getId().getValue());
        return new OrderApprovedEvent(order, ZonedDateTime.now(ZoneId.of(UTC)), orderApprovedEventDomainEventPublisher);
    }

    private void validateAdmin(Admin admin) {
        // validate admin
        if (admin == null) {
            log.error("Admin is null");
            throw new OrderDomainException("Admin is null");
        }

        if (!admin.isActive()) {
            log.error("Admin is not active");
            throw new OrderDomainException("Admin with id" + admin.getId().getValue() + "is not active");
        }
    }
}
