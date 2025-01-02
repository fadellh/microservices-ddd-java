package com.mwc.inventory.service.domain.dto.transfer;


import com.mwc.inventory.service.domain.event.StockDecrementedEvent;
import com.mwc.inventory.service.domain.event.StockIncrementedEvent;
import com.mwc.inventory.service.domain.event.StockUpdatedEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class StockTransferEventResult {
    private final StockUpdatedEvent stockDecrementedEvent;
    private final StockUpdatedEvent stockIncrementedEvent;
}
