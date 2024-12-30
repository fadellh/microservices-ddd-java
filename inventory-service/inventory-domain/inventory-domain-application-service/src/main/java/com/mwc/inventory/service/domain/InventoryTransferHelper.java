package com.mwc.inventory.service.domain;

import com.mwc.inventory.service.domain.dto.transfer.TransferInventoryCommand;
import com.mwc.inventory.service.domain.dto.transfer.TransferInventoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
public class InventoryTransferHelper {


    @Transactional
    public TransferInventoryResponse manualTransferInventory(UUID inventoryId, TransferInventoryCommand transferInventoryCommand) {

        return TransferInventoryResponse.builder()
                .build();
    }


}
