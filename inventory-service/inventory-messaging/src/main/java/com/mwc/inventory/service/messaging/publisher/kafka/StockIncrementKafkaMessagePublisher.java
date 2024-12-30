package com.mwc.inventory.service.messaging.publisher.kafka;

import com.mwc.inventory.service.domain.config.InventoryServiceConfigData;
import com.mwc.inventory.service.domain.event.StockIncrementedEvent;
import com.mwc.inventory.service.domain.ports.output.message.publisher.StockIncrementedMessagePublisher;
import com.mwc.inventory.service.messaging.mapper.InventoryMessagingDataMapper;
import com.mwc.kafka.KafkaMessageHelper;
import com.mwc.kafka.order.avro.model.StockIncrementAvroModel;
import com.mwc.kafka.service.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StockIncrementKafkaMessagePublisher implements StockIncrementedMessagePublisher {
    private final InventoryMessagingDataMapper inventoryMessagingDataMapper;
    private final InventoryServiceConfigData inventoryServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;
    private final KafkaProducer<String, StockIncrementAvroModel> kafkaProducer;

    public StockIncrementKafkaMessagePublisher(InventoryMessagingDataMapper inventoryMessagingDataMapper, InventoryServiceConfigData inventoryServiceConfigData, KafkaMessageHelper kafkaMessageHelper, KafkaProducer<String, StockIncrementAvroModel> kafkaProducer) {
        this.inventoryMessagingDataMapper = inventoryMessagingDataMapper;
        this.inventoryServiceConfigData = inventoryServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void publish(StockIncrementedEvent domainEvent) {
        String inventoryId = domainEvent.getInventory().getId().getValue().toString();
        log.info("Received StockIncrementedEvent for inventory id: {}", inventoryId);

        try {
            StockIncrementAvroModel stockIncrementAvroModel = inventoryMessagingDataMapper.stockIncrementedEventToStockIncrementAvroModel(domainEvent);
            kafkaProducer.send(inventoryServiceConfigData.getStockIncrementTopicName(),
                    inventoryId,
                    stockIncrementAvroModel,
                    kafkaMessageHelper.getKafkaCallback(inventoryServiceConfigData.getStockIncrementTopicName(),
                            stockIncrementAvroModel,
                            inventoryId,
                            "StockInc rementAvroModel"));
            log.info("StockIncrementAvroModel sent to Kafka for inventory id: {}", stockIncrementAvroModel.getId());
        } catch (Exception e) {
            log.error("Error while sending StockIncrementAvroModel message" +
                    " to kafka with inventory id: {}, error: {}", inventoryId, e.getMessage());
        }
    }
}
