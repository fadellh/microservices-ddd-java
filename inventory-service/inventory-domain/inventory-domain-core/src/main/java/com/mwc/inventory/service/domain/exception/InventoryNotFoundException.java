package com.mwc.inventory.service.domain.exception;

import com.mwc.domain.exception.DomainException;

public class InventoryNotFoundException extends DomainException {
    public InventoryNotFoundException(String message) {
        super(message);
    }
}