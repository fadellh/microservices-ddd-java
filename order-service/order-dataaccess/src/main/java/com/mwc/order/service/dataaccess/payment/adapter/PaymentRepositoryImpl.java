package com.mwc.order.service.dataaccess.payment.adapter;

import com.mwc.order.service.dataaccess.payment.entity.PaymentEntity;
import com.mwc.order.service.dataaccess.payment.mapper.PaymentDataAccessMapper;
import com.mwc.order.service.dataaccess.payment.repository.PaymentJpaRepository;
import com.mwc.order.service.domain.entity.Payment;
import com.mwc.order.service.domain.ports.output.repository.PaymentRepository;
import org.springframework.stereotype.Component;

@Component
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentDataAccessMapper paymentDataAccessMapper;

    public PaymentRepositoryImpl(PaymentJpaRepository paymentJpaRepository, PaymentDataAccessMapper paymentDataAccessMapper) {
        this.paymentJpaRepository = paymentJpaRepository;
        this.paymentDataAccessMapper = paymentDataAccessMapper;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity paymentEntity = paymentDataAccessMapper.paymentToPaymentEntity(payment);
        paymentEntity = paymentJpaRepository.save(paymentEntity);
        return paymentDataAccessMapper.paymentEntityToPayment(paymentEntity);
    }
}