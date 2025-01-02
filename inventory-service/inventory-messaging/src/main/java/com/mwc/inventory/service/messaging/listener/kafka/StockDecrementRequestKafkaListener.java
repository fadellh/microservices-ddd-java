package com.mwc.inventory.service.messaging.listener.kafka;

import com.mwc.inventory.service.domain.ports.input.message.listener.StockDecrementRequestMessageListener;
import com.mwc.inventory.service.messaging.mapper.InventoryMessagingDataMapper;
import com.mwc.kafka.consumer.KafkaConsumer;
import com.mwc.kafka.order.avro.model.StockDecrementAvroModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class StockDecrementRequestKafkaListener implements KafkaConsumer<StockDecrementAvroModel> {

    private final StockDecrementRequestMessageListener stockDecrementRequestMessageListener;
    private final InventoryMessagingDataMapper inventoryMessagingDataMapper;

    public StockDecrementRequestKafkaListener(StockDecrementRequestMessageListener stockDecrementRequestMessageListener, InventoryMessagingDataMapper inventoryMessagingDataMapper) {
        this.stockDecrementRequestMessageListener = stockDecrementRequestMessageListener;
        this.inventoryMessagingDataMapper = inventoryMessagingDataMapper;
    }

    @Override
    @KafkaListener(
//            id = "${kafka-consumer-config.stock-decrement-consumer-group-id}",
            groupId = "${kafka-consumer-config.stock-decrement-consumer-group-id}",
            topics = "${inventory-service.stock-decrement-request-topic-name}"
//            containerFactory = "kafkaListenerContainerFactory"
    )
    public void receive(
            @Payload List<StockDecrementAvroModel> messages,
            @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
            @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
            @Header(KafkaHeaders.OFFSET) List<Long> offsets
            ) {
        log.info("{} number of stock decrement requests received with keys:{}, partitions:{} and offsets: {} and messages:{}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString(),
                messages);

        messages.forEach(stockDecrementAvroModel -> {
            try {
                log.info("Processing stock decrement request for product id: {}  <#><#>>", stockDecrementAvroModel.getProductId());
                stockDecrementRequestMessageListener.decrementStock(
                        inventoryMessagingDataMapper.stockDecrementAvroModelToStockDecrementRequest(stockDecrementAvroModel)
                );
            } catch (Exception e) {
                log.error("Failed to process stock decrement for product id: {}", stockDecrementAvroModel.getProductId(), e);
            }
        });


    }
}
