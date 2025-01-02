package com.mwc.order.service.domain.ports.output.message.publisher;

import com.mwc.order.service.domain.event.OrderApproveFailedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NoOrderApproveFailedMessagePublisher implements OrderApproveFailedMessagePublisher {

    @Override
    public void publish(OrderApproveFailedEvent domainEvent) {
        // No-op
        log.info("No-op publisher for OrderApproveFailedEvent; ignoring event with ID: {}",
                domainEvent.getOrder().getId().getValue());
    }
}
