package com.mwc.order.service.domain;

import com.mwc.domain.event.EmptyEvent;
import com.mwc.order.service.domain.dto.message.StockDecrementResponse;
import com.mwc.order.service.domain.entity.Order;
import com.mwc.order.service.domain.event.OrderApproveFailedEvent;
import com.mwc.order.service.domain.ports.output.message.publisher.OrderApproveFailedMessagePublisher;
import com.mwc.saga.SagaStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class OrderApprovalSaga implements SagaStep<StockDecrementResponse, EmptyEvent, OrderApproveFailedEvent>{

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final OrderApproveFailedMessagePublisher orderApproveFailedMessagePublisher;

    public OrderApprovalSaga(OrderDomainService orderDomainService, OrderSagaHelper orderSagaHelper, OrderApproveFailedMessagePublisher orderApproveFailedMessagePublisher) {
        this.orderDomainService = orderDomainService;
        this.orderSagaHelper = orderSagaHelper;
        this.orderApproveFailedMessagePublisher = orderApproveFailedMessagePublisher;
    }

    @Override
    @Transactional
    public EmptyEvent process(StockDecrementResponse data) {
        log.info("Approving order with id: {}", data.getOrderId());
        Order order = orderSagaHelper.findOrder(data.getOrderId());
        orderDomainService.approveOrder(order);
        orderSagaHelper.saveOrder(order);
        log.info("Order with id: {} is approved", order.getId().getValue());
        return EmptyEvent.INSTANCE;
    }

    @Override
    public OrderApproveFailedEvent rollback(StockDecrementResponse data) {
        log.info("Rolling back order with id: {}", data.getOrderId());
        Order order = orderSagaHelper.findOrder(data.getOrderId());
        OrderApproveFailedEvent domainEvent = orderDomainService.cancelApproveOrder(order, data.getFailureMessage(),orderApproveFailedMessagePublisher);
        orderSagaHelper.saveOrder(order);
        log.info("Order with id: {} is rolled back", order.getId().getValue());
        return domainEvent;
    }
}
