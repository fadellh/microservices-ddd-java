package com.mwc.order.service.dataaccess.payment.repository;

import com.mwc.order.service.dataaccess.payment.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {
}
