package com.mwc.inventory.service.domain;

import com.mwc.inventory.service.domain.event.StockDeductedEvent;
import com.mwc.inventory.service.domain.ports.input.message.listener.order.StockDeductRequestMessageListener;

public class StockDeductRequestMessageListenerImpl implements StockDeductRequestMessageListener {
    @Override
    public void publish(StockDeductedEvent domainEvent) {

    }

}
