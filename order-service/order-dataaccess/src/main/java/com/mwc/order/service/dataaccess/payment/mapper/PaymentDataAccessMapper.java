package com.mwc.order.service.dataaccess.payment.mapper;

import com.mwc.domain.valueobject.*;
import com.mwc.order.service.dataaccess.payment.entity.PaymentEntity;
import com.mwc.order.service.domain.entity.Payment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Component
public class PaymentDataAccessMapper {

    public PaymentEntity paymentToPaymentEntity(Payment payment) {
        LocalDateTime paymentDate = LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
        return PaymentEntity.builder()
                .id(UUID.randomUUID())
                .orderId(payment.getOrderId().getValue().toString())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getStatus())
                .paymentAmount(payment.getAmount().getAmount())
                .paymentProofUrl(payment.getProofUrl())
                .paymentDate(paymentDate)
                .build();
    }

    public Payment paymentEntityToPayment(PaymentEntity paymentEntity) {
        return Payment.builder()
                .paymentId(new PaymentId(paymentEntity.getId()))
                .orderId(new OrderId(UUID.fromString(paymentEntity.getOrderId())))
                .amount(new Money(paymentEntity.getPaymentAmount()))
                .status(paymentEntity.getPaymentStatus())
                .paymentMethod(paymentEntity.getPaymentMethod())
                .build();
    }
}