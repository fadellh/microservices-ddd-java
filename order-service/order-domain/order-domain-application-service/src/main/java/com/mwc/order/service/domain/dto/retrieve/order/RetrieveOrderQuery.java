package com.mwc.order.service.domain.dto.retrieve.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class RetrieveOrderQuery {
    @NotNull
    private final UUID customerId;
    private final UUID orderNumber; // Optional order number filter
    private final String orderStartDate; // Optional start date filter
    private final String orderEndDate; // Optional end date filter
}
