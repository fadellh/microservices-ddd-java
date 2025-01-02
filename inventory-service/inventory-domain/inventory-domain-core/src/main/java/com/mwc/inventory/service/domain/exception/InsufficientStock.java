package com.mwc.inventory.service.domain.exception;

import com.mwc.domain.exception.DomainException;

public class InsufficientStock extends DomainException {
    public InsufficientStock(String message) {
        super(message);
    }
}
