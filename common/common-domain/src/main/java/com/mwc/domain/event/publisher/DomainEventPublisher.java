package com.mwc.domain.event.publisher;

import com.mwc.domain.event.DomainEvent;

public interface DomainEventPublisher<T extends DomainEvent> {
    void publish(T domainEvent);
}
