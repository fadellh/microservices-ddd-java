package com.mwc.order.service.domain.ports.output.message.publisher;

import com.mwc.order.service.domain.event.OrderStatusUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NoOpOrderStatusUpdatedMessagePublisher implements OrderStatusUpdatedMessagePublisher {

    @Override
    public void publish(OrderStatusUpdatedEvent domainEvent) {
        // do nothing for now
        log.info("No-op publisher for OrderStatusUpdatedEvent; ignoring event with ID: {}",
                domainEvent.getOrder().getId().getValue());
    }
}
