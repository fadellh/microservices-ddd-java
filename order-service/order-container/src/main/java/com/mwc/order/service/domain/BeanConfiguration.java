package com.mwc.order.service.domain;


import com.mwc.kafka.KafkaMessageHelper;
import com.mwc.kafka.order.avro.model.OrderCreateAvroModel;
import com.mwc.kafka.service.KafkaProducer;
import com.mwc.kafka.service.impl.KafkaProducerImpl;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class BeanConfiguration {

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }

//    @Bean
//    public KafkaProducer<String, OrderCreateAvroModel> kafkaProducer(KafkaTemplate<String, OrderCreateAvroModel> kafkaTemplate) {
//        return new KafkaProducerImpl<>(kafkaTemplate);
//    }
//
//    @Bean
//    public KafkaMessageHelper kafkaMessageHelper() {
//        return new KafkaMessageHelper();
//    }
}