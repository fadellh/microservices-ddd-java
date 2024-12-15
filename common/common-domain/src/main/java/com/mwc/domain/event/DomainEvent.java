package com.mwc.domain.event;

import java.time.ZonedDateTime;

public interface DomainEvent<T> {
    T getEntity();
    ZonedDateTime getCreatedAt();
    void fire();
}
