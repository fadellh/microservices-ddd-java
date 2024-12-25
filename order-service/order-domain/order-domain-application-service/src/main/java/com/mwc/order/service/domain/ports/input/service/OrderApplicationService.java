package com.mwc.order.service.domain.ports.input.service;

import com.mwc.order.service.domain.dto.create.CreateOrderCommand;
import com.mwc.order.service.domain.dto.create.CreateOrderResponse;
import com.mwc.order.service.domain.dto.create.PreviewOrderCommand;
import com.mwc.order.service.domain.dto.create.PreviewOrderResponse;
import com.mwc.order.service.domain.dto.create.payment.CreatePaymentCommand;
import com.mwc.order.service.domain.dto.create.payment.CreatePaymentResponse;

import javax.validation.Valid;

public interface OrderApplicationService {
    CreateOrderResponse createOrder(@Valid CreateOrderCommand createOrderCommand);

    PreviewOrderResponse previewOrder(@Valid PreviewOrderCommand previewOrderCommand);

    CreatePaymentResponse createPayment(@Valid CreatePaymentCommand createPaymentCommand);
}
