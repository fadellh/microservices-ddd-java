package com.mwc.order.service.dataaccess.payment.entity;

import com.mwc.domain.valueobject.PaymentMethod;
import com.mwc.domain.valueobject.PaymentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments", schema = "payment")
@Entity
public class PaymentEntity {
    @Id
    private UUID id;
    private String orderId;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private LocalDateTime paymentDate;
    private BigDecimal paymentAmount;
    private String paymentReference;
    private String paymentProofUrl;
}