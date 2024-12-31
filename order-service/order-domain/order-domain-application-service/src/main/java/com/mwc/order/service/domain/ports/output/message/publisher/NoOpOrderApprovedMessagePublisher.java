package com.mwc.order.service.domain.ports.output.message.publisher;

import com.mwc.order.service.domain.event.OrderApprovedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NoOpOrderApprovedMessagePublisher implements OrderApprovedDecrementStockRequestMessagePublisher {

    @Override
    public void publish(OrderApprovedEvent domainEvent) {
        // No-op
        log.info("No-op publisher for OrderApprovedEvent; ignoring event with ID: {}",
                domainEvent.getOrder().getId().getValue());
    }
}
