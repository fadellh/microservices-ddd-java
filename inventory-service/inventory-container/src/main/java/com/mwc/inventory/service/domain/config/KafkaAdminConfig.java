package com.mwc.inventory.service.domain.config;

import com.mwc.kafka.config.data.KafkaConfigData;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaAdminConfig {

    private final KafkaConfigData kafkaConfigData;

    public KafkaAdminConfig(KafkaConfigData kafkaConfigData) {
        this.kafkaConfigData = kafkaConfigData;
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());
//        configs.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, kafkaConfigData.getSecurityProtocol());
//        configs.put("sasl.mechanism", kafkaConfigData.getSaslMechanism());
//        configs.put("sasl.jaas.config", kafkaConfigData.getSaslJaasConfig());
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic stockDecrementRequestTopic(@Value("${inventory-service.stock-decrement-request-topic-name}") String topicName) {
        return TopicBuilder.name(topicName)
                .partitions(kafkaConfigData.getNumOfPartitions())
                .replicas(kafkaConfigData.getReplicationFactor())
                .build();
    }

    @Bean
    public NewTopic stockDecrementResponseTopic(@Value("${inventory-service.stock-decrement-response-topic-name}") String topicName) {
        return TopicBuilder.name(topicName)
                .partitions(kafkaConfigData.getNumOfPartitions())
                .replicas(kafkaConfigData.getReplicationFactor())
                .build();
    }

    @Bean
    public NewTopic stockIncrementRequestTopic(@Value("${inventory-service.stock-increment-topic-name}") String topicName) {
        return TopicBuilder.name(topicName)
                .partitions(kafkaConfigData.getNumOfPartitions())
                .replicas(kafkaConfigData.getReplicationFactor())
                .build();
    }

    @Bean
    public NewTopic stockIncrementResponseTopic(@Value("${inventory-service.stock-increment-response-topic-name}") String topicName) {
        return TopicBuilder.name(topicName)
                .partitions(kafkaConfigData.getNumOfPartitions())
                .replicas(kafkaConfigData.getReplicationFactor())
                .build();
    }
}
