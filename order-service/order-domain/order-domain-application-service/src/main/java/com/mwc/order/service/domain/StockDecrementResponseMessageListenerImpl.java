package com.mwc.order.service.domain;

import com.mwc.order.service.domain.dto.message.StockDecrementResponse;
import com.mwc.order.service.domain.ports.input.message.listener.StockDecrementResponseMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Slf4j
@Validated
@Service
public class StockDecrementResponseMessageListenerImpl implements StockDecrementResponseMessageListener {

    private final OrderApprovalSaga orderApprovalSaga;

    public StockDecrementResponseMessageListenerImpl(OrderApprovalSaga orderApprovalSaga) {
        this.orderApprovalSaga = orderApprovalSaga;
    }

    @Override
    public void stockDecrementSuccess(StockDecrementResponse stockDecrementResponse) {
        orderApprovalSaga.process(stockDecrementResponse);
        log.info("Stock decrement success for order id: {}", stockDecrementResponse.getOrderId());
    }

    @Override
    public void stockDecrementFailed(StockDecrementResponse stockDecrementResponse) {
        orderApprovalSaga.rollback(stockDecrementResponse);
        log.info("Stock decrement failed for order id: {}", stockDecrementResponse);
    }
}
