package com.mwc.order.service.messaging.publisher.kafka;

import com.mwc.kafka.KafkaMessageHelper;
import com.mwc.kafka.order.avro.model.StockDecrementAvroModel;
import com.mwc.kafka.service.KafkaProducer;
import com.mwc.order.service.domain.config.OrderServiceConfigData;
import com.mwc.order.service.domain.event.OrderApprovedEvent;
import com.mwc.order.service.domain.ports.output.message.publisher.OrderApprovedDecrementStockRequestMessagePublisher;
import com.mwc.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApproveOrderKafkaMessagePublisher implements OrderApprovedDecrementStockRequestMessagePublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;
    private final KafkaProducer<String, StockDecrementAvroModel> kafkaProducer;



    public ApproveOrderKafkaMessagePublisher(OrderMessagingDataMapper orderMessagingDataMapper, OrderServiceConfigData orderServiceConfigData, KafkaMessageHelper kafkaMessageHelper, KafkaProducer<String, StockDecrementAvroModel> kafkaProducer) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void publish(OrderApprovedEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getValue().toString();
        log.info("Received OrderApprovedEvent for order id: {}", orderId);

        domainEvent.getOrder().getItems().forEach(orderItem -> {
//            log.info("Product item id: {}, quantity: {}", orderItem.getProduct().getId().getValue(), orderItem.getQuantity());
            try {
                StockDecrementAvroModel stockDecrementAvroModel = orderMessagingDataMapper
                        .orderApprovedEventToStockDecrementAvroModel(domainEvent, orderItem);

                kafkaProducer.send(orderServiceConfigData.getStockDecrementRequestTopicName(),
                        orderId,
                        stockDecrementAvroModel,
                        kafkaMessageHelper
                                .getKafkaCallback(orderServiceConfigData.getStockDecrementResponseTopicName(),
                                        stockDecrementAvroModel,
                                        orderId,
                                        "StockDecrementAvroModel")
                );

                log.info("StockDecrementAvroModel sent to Kafka for order id: {}", stockDecrementAvroModel.getId());
                log.info("Stock Decrement requested sent to Kafka");
            } catch (Exception e) {
                log.error("Error while sending StockDecrementAvroModel message" +
                        " to kafka with order id: {}, error: {}", orderId, e.getMessage());
            }
        });

    }
}
