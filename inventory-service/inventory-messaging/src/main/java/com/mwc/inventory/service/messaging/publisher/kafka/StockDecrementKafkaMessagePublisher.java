package com.mwc.inventory.service.messaging.publisher.kafka;

import com.mwc.inventory.service.domain.config.InventoryServiceConfigData;
import com.mwc.inventory.service.domain.event.StockDecrementedEvent;
import com.mwc.inventory.service.domain.ports.output.message.publisher.StockDecrementedMessagePublisher;
import com.mwc.inventory.service.messaging.mapper.InventoryMessagingDataMapper;
import com.mwc.kafka.order.avro.model.StockDecrementAvroModel;
import com.mwc.kafka.service.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.mwc.kafka.KafkaMessageHelper;

@Slf4j
@Component
public class StockDecrementKafkaMessagePublisher implements StockDecrementedMessagePublisher {
    private final InventoryMessagingDataMapper inventoryMessagingDataMapper;
    private final InventoryServiceConfigData inventoryServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;
    private final KafkaProducer<String, StockDecrementAvroModel> kafkaProducer;

    public StockDecrementKafkaMessagePublisher(InventoryMessagingDataMapper inventoryMessagingDataMapper, InventoryServiceConfigData inventoryServiceConfigData, KafkaMessageHelper kafkaMessageHelper, KafkaProducer<String, StockDecrementAvroModel> kafkaProducer) {
        this.inventoryMessagingDataMapper = inventoryMessagingDataMapper;
        this.inventoryServiceConfigData = inventoryServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
        this.kafkaProducer = kafkaProducer;
    }


    @Override
    public void publish(StockDecrementedEvent domainEvent) {
        String inventoryId = domainEvent.getInventory().getId().getValue().toString();
        log.info("Received StockDecrementedEvent for inventory id: {}", inventoryId);

        try {
            StockDecrementAvroModel stockDecrementAvroModel = inventoryMessagingDataMapper.stockDecrementedEventToStockDecrementAvroModel(domainEvent);
            kafkaProducer.send(inventoryServiceConfigData.getStockDecrementTopicName(),
                    inventoryId,
                    stockDecrementAvroModel,
                    kafkaMessageHelper.getKafkaCallback(inventoryServiceConfigData.getStockDecrementTopicName(),
                            stockDecrementAvroModel,
                            inventoryId,
                            "StockDecrementAvroModel"));
            log.info("StockDecrementAvroModel sent to Kafka for inventory id: {}", stockDecrementAvroModel.getId());
        } catch (Exception e) {
            log.error("Error while sending StockDecrementAvroModel message" +
                    " to kafka with inventory id: {}, error: {}", inventoryId, e.getMessage());
        }
    }

}
