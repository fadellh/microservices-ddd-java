package com.mwc.order.service.domain;

import com.mwc.order.service.domain.dto.create.*;
import com.mwc.order.service.domain.dto.create.payment.CreatePaymentCommand;
import com.mwc.order.service.domain.dto.create.payment.CreatePaymentResponse;
import com.mwc.order.service.domain.dto.retrieve.order.RetrieveOrderQuery;
import com.mwc.order.service.domain.dto.retrieve.order.RetrieveOrderQueryResponse;
import com.mwc.order.service.domain.dto.retrieve.order.RetrieveOrderDetailQueryResponse;
import com.mwc.order.service.domain.ports.input.service.OrderApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Service
@Validated
public class OrderApplicationServiceImpl implements OrderApplicationService {

    private final OrderPreviewCommandHandler orderPreviewCommandHandler;
    private final OrderCreateCommandHandler orderCreateCommandHandler;
    private final OrderQueryHandler orderQueryHandler;


    public OrderApplicationServiceImpl(OrderPreviewCommandHandler orderPreviewCommandHandler, OrderCreateCommandHandler orderCreateCommandHandler, OrderQueryHandler orderQueryHandler) {
        this.orderPreviewCommandHandler = orderPreviewCommandHandler;
        this.orderCreateCommandHandler = orderCreateCommandHandler;
        this.orderQueryHandler = orderQueryHandler;
    }

    @Override
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        // Implementation for create order
        return orderCreateCommandHandler.createOrder(createOrderCommand);
    }

    @Override
    public PreviewOrderResponse previewOrder(PreviewOrderCommand previewOrderCommand) {
        return orderPreviewCommandHandler.previewOrder(previewOrderCommand);
    }

    @Override
    public CreatePaymentResponse createPayment(CreatePaymentCommand createPaymentCommand) {
        // Implementation for create payment
        return orderCreateCommandHandler.createPayment(createPaymentCommand);
    }

    @Override
    public UpdateOrderStatusResponse updateOrderStatus(UpdateOrderStatusCommand updateOrderStatusCommand) {
        // Implementation for update order status
        return orderCreateCommandHandler.updateOrderStatus(updateOrderStatusCommand);
    }

    @Override
    public List<RetrieveOrderQueryResponse> retrieveOrders(RetrieveOrderQuery orderListQuery) {
        // Implementation for retrieve orders
        return orderQueryHandler.retrieveOrders(orderListQuery);
    }

    @Override
    public RetrieveOrderDetailQueryResponse retrieveOrder(UUID orderId) {
        // Implementation for retrieve order
        return orderQueryHandler.retrieveOrder(orderId);
    }

}