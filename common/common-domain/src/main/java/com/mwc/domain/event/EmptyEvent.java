package com.mwc.domain.event;

import java.time.ZonedDateTime;

public final class EmptyEvent implements DomainEvent<Void> {

    public static final EmptyEvent INSTANCE = new EmptyEvent();

    private EmptyEvent() {
    }

    @Override
    public Void getEntity() {
        return null;
    }

    @Override
    public ZonedDateTime getCreatedAt() {
        return null;
    }

    @Override
    public void fire() {

    }
}