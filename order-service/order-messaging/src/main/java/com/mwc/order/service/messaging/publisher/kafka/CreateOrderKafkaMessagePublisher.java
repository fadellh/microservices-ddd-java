package com.mwc.order.service.messaging.publisher.kafka;

import com.mwc.kafka.order.avro.model.OrderCreateAvroModel;
import com.mwc.kafka.KafkaMessageHelper;
import com.mwc.kafka.service.KafkaProducer;
import com.mwc.order.service.domain.config.OrderServiceConfigData;
import com.mwc.order.service.domain.event.OrderCreatedEvent;
import com.mwc.order.service.domain.ports.output.message.publisher.OrderCreatedMessagePublisher;
import com.mwc.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateOrderKafkaMessagePublisher implements OrderCreatedMessagePublisher {
    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, OrderCreateAvroModel> kafkaProducer;
    private final KafkaMessageHelper kafkaMessageHelper;

    public CreateOrderKafkaMessagePublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                            OrderServiceConfigData orderServiceConfigData,
                                            KafkaProducer<String, OrderCreateAvroModel> kafkaProducer,
                                            KafkaMessageHelper kafkaMessageHelper) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaProducer = kafkaProducer;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    public void publish(OrderCreatedEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getValue().toString();
        log.info("Received OrderCreatedEvent for order id: {}", orderId);

        try {
            OrderCreateAvroModel orderCreateAvroModel = orderMessagingDataMapper
                    .orderCreatedEventToOrderCreateAvroModel(domainEvent);

            kafkaProducer.send(orderServiceConfigData.getOrderCreateTopicName(),
                    orderId,
                    orderCreateAvroModel,
                    kafkaMessageHelper
                            .getKafkaCallback(orderServiceConfigData.getOrderCreateResponseTopicName(),
                                    orderCreateAvroModel,
                                    orderId,
                                    "OrderCreateAvroModel")
            );

            log.info("OrderCreateAvroModel sent to Kafka for order id: {}", orderCreateAvroModel.getId());
        } catch (Exception e) {
            log.error("Error while sending OrderCreateAvroModel message" +
                    " to kafka with order id: {}, error: {}", orderId, e.getMessage());
        }

        log.info("Order requested sent to Kafka");
    }
}