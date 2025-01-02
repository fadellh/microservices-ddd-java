package com.mwc.inventory.service.messaging.publisher.kafka;

import com.mwc.inventory.service.domain.config.InventoryServiceConfigData;
import com.mwc.inventory.service.domain.event.OrderStockFailedEvent;
import com.mwc.inventory.service.domain.ports.output.message.publisher.OrderStockFailedMessagePublisher;
import com.mwc.inventory.service.messaging.mapper.InventoryMessagingDataMapper;
import com.mwc.kafka.KafkaMessageHelper;
import com.mwc.kafka.order.avro.model.StockDecrementResponseAvroModel;
import com.mwc.kafka.service.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StockOrderFailedKafkaMessagePublisher implements OrderStockFailedMessagePublisher {
    private final InventoryMessagingDataMapper inventoryMessagingDataMapper;
    private final InventoryServiceConfigData inventoryServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;
    private final KafkaProducer<String, StockDecrementResponseAvroModel> kafkaProducer;

    public StockOrderFailedKafkaMessagePublisher(InventoryMessagingDataMapper inventoryMessagingDataMapper, InventoryServiceConfigData inventoryServiceConfigData, KafkaMessageHelper kafkaMessageHelper, KafkaProducer<String, StockDecrementResponseAvroModel> kafkaProducer) {
        this.inventoryMessagingDataMapper = inventoryMessagingDataMapper;
        this.inventoryServiceConfigData = inventoryServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void publish(OrderStockFailedEvent event) {
        String orderId = event.getInventory().getOrderId().getValue().toString();
        log.info("Received OrderStockFailedEvent for order id: {}", orderId);

        try {
            StockDecrementResponseAvroModel stockDecrementResponseAvroModel = inventoryMessagingDataMapper.orderStockFailedEventToStockDecrementResponseAvroModel(event);
            kafkaProducer.send(inventoryServiceConfigData.getStockDecrementResponseTopicName(),
                    orderId,
                    stockDecrementResponseAvroModel,
                    kafkaMessageHelper.getKafkaCallback(inventoryServiceConfigData.getStockDecrementResponseTopicName(),
                            stockDecrementResponseAvroModel,
                            orderId,
                            "StockDecrementResponseAvroModel"));
            log.info("StockDecrementResponseAvroModel sent to Kafka for order id: {}", stockDecrementResponseAvroModel.getOrderId());
        } catch (Exception e) {
            log.error("Error while sending StockDecrementResponseAvroModel message" +
                    " to kafka with order id: {}, error: {}", orderId, e.getMessage());
        }

    }
}
