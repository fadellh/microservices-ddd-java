package com.mwc.inventory.service.messaging.mapper;

import com.mwc.domain.valueobject.InventoryId;
import com.mwc.domain.valueobject.ProductId;
import com.mwc.domain.valueobject.WarehouseId;
import com.mwc.inventory.service.domain.dto.message.StockDecrementRequest;
import com.mwc.inventory.service.domain.entity.Inventory;
import com.mwc.inventory.service.domain.event.OrderStockCompletedEvent;
import com.mwc.inventory.service.domain.event.OrderStockFailedEvent;
import com.mwc.inventory.service.domain.event.StockDecrementedEvent;
import com.mwc.inventory.service.domain.event.StockIncrementedEvent;
import com.mwc.kafka.order.avro.model.OrderItemRequest;
import com.mwc.kafka.order.avro.model.StockDecrementAvroModel;
import com.mwc.kafka.order.avro.model.StockDecrementResponseAvroModel;
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

    public StockDecrementRequest stockDecrementAvroModelToStockDecrementRequest(StockDecrementAvroModel stockDecrementAvroModel) {
        return StockDecrementRequest.builder()
                .id(stockDecrementAvroModel.getId().toString())
                .sagaId(stockDecrementAvroModel.getSagaId().toString())
                .inventoryId(stockDecrementAvroModel.getInventoryId())
                .productId(stockDecrementAvroModel.getProductId())
                .warehouseId(stockDecrementAvroModel.getWarehouseId())
                .orderId(stockDecrementAvroModel.getOrderId())
                .quantity(stockDecrementAvroModel.getQuantity())
                .createdAt(stockDecrementAvroModel.getCreatedAt())
                .build();

    }

    public StockDecrementResponseAvroModel orderStockCompletedEventToStockDecrementResponseAvroModel(OrderStockCompletedEvent domainEvent) {
        Inventory inventory = domainEvent.getInventory();
        return StockDecrementResponseAvroModel.newBuilder()
                .setId(inventory.getId().getValue())
                .setSagaId(UUID.randomUUID())
                .setInventoryId(inventory.getId().getValue())
                .setOrderId(inventory.getOrderId().getValue())
                .setCreatedAt(domainEvent.getCreatedAt().toInstant())
                .setFailureMessages(inventory.getFailureMessages())
                .build();
    }

    public StockDecrementResponseAvroModel orderStockFailedEventToStockDecrementResponseAvroModel(OrderStockFailedEvent event) {
        Inventory inventory = event.getInventory();
        return StockDecrementResponseAvroModel.newBuilder()
                .setId(inventory.getId().getValue())
                .setSagaId(UUID.randomUUID())
                .setInventoryId(inventory.getId().getValue())
                .setOrderId(inventory.getOrderId().getValue())
                .setFailureMessages(inventory.getFailureMessages())
                .setCreatedAt(event.getCreatedAt().toInstant())
                .build();
    }
}
