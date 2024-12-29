package com.mwc.order.service.domain.ports.input.service;

import com.mwc.order.service.domain.dto.create.*;
import com.mwc.order.service.domain.dto.create.payment.CreatePaymentCommand;
import com.mwc.order.service.domain.dto.create.payment.CreatePaymentResponse;
import com.mwc.order.service.domain.dto.retrieve.order.RetrieveOrderDetailQueryResponse;
import com.mwc.order.service.domain.dto.retrieve.order.RetrieveOrderQuery;
import com.mwc.order.service.domain.dto.retrieve.order.RetrieveOrderQueryResponse;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface OrderApplicationService {
    CreateOrderResponse createOrder(@Valid CreateOrderCommand createOrderCommand);

    PreviewOrderResponse previewOrder(@Valid PreviewOrderCommand previewOrderCommand);

    CreatePaymentResponse createPayment(@Valid CreatePaymentCommand createPaymentCommand);

    UpdateOrderStatusResponse updateOrderStatus(@Valid UpdateOrderStatusCommand updateOrderStatusCommand);

    List<RetrieveOrderQueryResponse> retrieveOrders(RetrieveOrderQuery orderListQuery);

    RetrieveOrderDetailQueryResponse retrieveOrder(UUID orderId);
}
