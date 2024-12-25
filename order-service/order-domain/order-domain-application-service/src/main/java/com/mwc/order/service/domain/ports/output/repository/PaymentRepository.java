package com.mwc.order.service.domain.ports.output.repository;

import com.mwc.order.service.domain.entity.Payment;

public interface PaymentRepository {
    Payment save(Payment payment);
}
