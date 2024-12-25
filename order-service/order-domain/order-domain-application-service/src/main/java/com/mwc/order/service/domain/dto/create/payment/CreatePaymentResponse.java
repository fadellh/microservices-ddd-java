package com.mwc.order.service.domain.dto.create.payment;

import com.mwc.domain.valueobject.OrderStatus;
import com.mwc.domain.valueobject.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@Builder
public class CreatePaymentResponse {
    @NotNull
    private String paymentProofUrl;
    @NotNull
    private final PaymentStatus paymentStatus;
    @NotNull
    private final String message;

}
