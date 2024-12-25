package com.mwc.order.service.domain.dto.create.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class CreatePaymentCommand {
    @NotNull
    private UUID orderId;
    @NotNull
    private MultipartFile paymentProofFile;
}