package com.mwc.domain.valueobject;

import java.util.UUID;

public class PaymentId extends BaseId<UUID> {

    public PaymentId(UUID id) {
        super(id);
    }
}
