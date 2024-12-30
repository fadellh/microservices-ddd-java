package com.mwc.inventory.service.messaging.mapper;

import com.mwc.inventory.service.domain.entity.Inventory;
import com.mwc.inventory.service.domain.event.StockDecrementedEvent;
import com.mwc.inventory.service.domain.event.StockIncrementedEvent;
import com.mwc.kafka.order.avro.model.StockDecrementAvroModel;
import com.mwc.kafka.order.avro.model.StockIncrementAvroModel;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class InventoryMessagingDataMapper {
    public StockDecrementAvroModel stockDecrementedEventToStockDecrementAvroModel(StockDecrementedEvent stockDecrementedEvent) {
        Inventory inventory = stockDecrementedEvent.getInventory();
        return StockDecrementAvroModel.newBuilder()
                .setId(inventory.getId().getValue())
                .setSagaId(UUID.randomUUID())
                .setInventoryId(inventory.getId().getValue())
                .setProductId(inventory.getProductId().getValue())
                .setWarehouseId(inventory.getWarehouseId().getValue())
                .setQuantity(inventory.getQuantity().getValue())
                .setCreatedAt(stockDecrementedEvent.getCreatedAt().toInstant())
                .build();
    }

    public StockIncrementAvroModel stockIncrementedEventToStockIncrementAvroModel(StockIncrementedEvent stockIncrementedEvent) {
        Inventory inventory = stockIncrementedEvent.getInventory();
        return StockIncrementAvroModel.newBuilder()
                .setId(inventory.getId().getValue())
                .setSagaId(UUID.randomUUID())
                .setInventoryId(inventory.getId().getValue())
                .setProductId(inventory.getProductId().getValue())
                .setWarehouseId(inventory.getWarehouseId().getValue())
                .setQuantity(inventory.getQuantity().getValue())
                .setCreatedAt(stockIncrementedEvent.getCreatedAt().toInstant())
                .build();
    }
}
