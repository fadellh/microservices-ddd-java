package com.mwc.order.service.domain;

import com.mwc.order.service.domain.dto.create.CreateOrderCommand;
import com.mwc.order.service.domain.dto.create.CreateOrderResponse;
import com.mwc.order.service.domain.dto.create.PreviewOrderCommand;
import com.mwc.order.service.domain.dto.create.PreviewOrderResponse;
import com.mwc.order.service.domain.dto.create.payment.CreatePaymentCommand;
import com.mwc.order.service.domain.dto.create.payment.CreatePaymentResponse;
import com.mwc.order.service.domain.ports.input.service.OrderApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class OrderApplicationServiceImpl implements OrderApplicationService {

    private final OrderPreviewCommandHandler orderPreviewCommandHandler;
    private final OrderCreateCommandHandler orderCreateCommandHandler;


    public OrderApplicationServiceImpl(OrderPreviewCommandHandler orderPreviewCommandHandler, OrderCreateCommandHandler orderCreateCommandHandler) {
        this.orderPreviewCommandHandler = orderPreviewCommandHandler;
        this.orderCreateCommandHandler = orderCreateCommandHandler;
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

}