package com.mwc.order.service.domain;

import com.mwc.order.service.domain.dto.create.CreateOrderCommand;
import com.mwc.order.service.domain.dto.create.CreateOrderResponse;
import com.mwc.order.service.domain.dto.create.PreviewOrderCommand;
import com.mwc.order.service.domain.dto.create.PreviewOrderResponse;
import com.mwc.order.service.domain.ports.input.service.OrderApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class OrderApplicationServiceImpl implements OrderApplicationService {

    private final OrderPreviewCommandHandler orderPreviewCommandHandler;

    public OrderApplicationServiceImpl(OrderPreviewCommandHandler orderPreviewCommandHandler) {
        this.orderPreviewCommandHandler = orderPreviewCommandHandler;
    }

    @Override
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        // Implementation for create order
        return null;
    }

    @Override
    public PreviewOrderResponse previewOrder(PreviewOrderCommand previewOrderCommand) {
        return orderPreviewCommandHandler.previewOrder(previewOrderCommand);
    }
}