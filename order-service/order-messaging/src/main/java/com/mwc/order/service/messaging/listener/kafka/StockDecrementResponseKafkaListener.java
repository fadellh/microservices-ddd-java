package com.mwc.order.service.messaging.listener.kafka;

import com.mwc.kafka.consumer.KafkaConsumer;
import com.mwc.kafka.order.avro.model.StockDecrementResponseAvroModel;
import com.mwc.order.service.domain.ports.input.message.listener.StockDecrementResponseMessageListener;
import com.mwc.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class StockDecrementResponseKafkaListener implements KafkaConsumer<StockDecrementResponseAvroModel> {

    private final StockDecrementResponseMessageListener stockDecrementResponseMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    public StockDecrementResponseKafkaListener(StockDecrementResponseMessageListener stockDecrementResponseMessageListener, OrderMessagingDataMapper orderMessagingDataMapper) {
        this.stockDecrementResponseMessageListener = stockDecrementResponseMessageListener;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
    }


    @Override
    @KafkaListener(id = "${kafka-consumer-config.stock-consumer-group-id}",
            topics = "${order-service.stock-decrement-response-topic-name}")
    public void receive(
            @Payload List<StockDecrementResponseAvroModel> messages,
            @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
            @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
            @Header(KafkaHeaders.OFFSET) List<Long> offsets
    ) {
        log.info("{} number of stock decrement responses received with keys:{}, partitions:{} and offsets: {} and messages:{}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString(),
                messages);

        messages.forEach(stockDecrementResponseAvroModel -> {
            if (stockDecrementResponseAvroModel.getFailureMessages().isEmpty()) {
                stockDecrementResponseMessageListener.stockDecrementSuccess(orderMessagingDataMapper.stockDecrementResponseAvroModelToStockDecrementResponse(stockDecrementResponseAvroModel));
            } else {
                stockDecrementResponseMessageListener.stockDecrementFailed(orderMessagingDataMapper.stockDecrementResponseAvroModelToStockDecrementResponse(stockDecrementResponseAvroModel));
            }
        });

    }


}
