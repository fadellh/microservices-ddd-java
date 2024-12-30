package com.mwc.inventory.service.domain.exception;

import com.mwc.domain.exception.DomainException;

public class NotEnoughInventoryException extends DomainException {
    public NotEnoughInventoryException(String message) {
        super(message);
    }
}
