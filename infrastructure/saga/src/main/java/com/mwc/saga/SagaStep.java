package com.mwc.saga;

import com.mwc.domain.event.DomainEvent;

public interface SagaStep<T, S extends DomainEvent, U extends DomainEvent> {
    S process(T  data);
    U rollback(T data);
}