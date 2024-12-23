package com.mwc.order.service.domain.ports.output.message.publisher;

import com.mwc.order.service.domain.event.OrderCreatedEvent;
import com.mwc.order.service.domain.ports.output.message.publisher.OrderCreatedMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NoOpOrderCreatedMessagePublisher implements OrderCreatedMessagePublisher {

    @Override
    public void publish(OrderCreatedEvent domainEvent) {
        // do nothing for now
        log.info("No-op publisher for OrderCreatedEvent; ignoring event with ID: {}",
                domainEvent.getOrder().getId().getValue());
    }
}
