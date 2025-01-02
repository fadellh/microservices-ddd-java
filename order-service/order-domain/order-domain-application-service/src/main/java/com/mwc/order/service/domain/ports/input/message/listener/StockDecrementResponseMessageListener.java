package com.mwc.order.service.domain.ports.input.message.listener;

import com.mwc.order.service.domain.dto.message.StockDecrementResponse;

import java.util.UUID;

public interface StockDecrementResponseMessageListener {
    void stockDecrementSuccess(StockDecrementResponse stockDecrementResponse);

    void stockDecrementFailed(StockDecrementResponse stockDecrementResponse);
}
