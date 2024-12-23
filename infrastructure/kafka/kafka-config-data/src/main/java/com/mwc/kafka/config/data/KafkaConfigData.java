package com.mwc.kafka.config.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-config")
public class KafkaConfigData {

    private String bootstrapServers;
    private String schemaRegistryUrlKey;
    private String schemaRegistryUrl;
    private Integer numOfPartitions;
    private Short replicationFactor;

    // 1) Add these fields for SASL/SSL config
    private String securityProtocol;   // e.g. SASL_SSL
    private String saslMechanism;      // e.g. PLAIN
    private String saslJaasConfig;     // e.g. org.apache.kafka.common.security.plain.PlainLoginModule ...
}
