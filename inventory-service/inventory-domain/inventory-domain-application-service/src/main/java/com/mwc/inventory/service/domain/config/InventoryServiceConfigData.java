package com.mwc.inventory.service.domain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "inventory-service")
public class InventoryServiceConfigData {
    private String stockDecrementTopicName;
    private String stockDecrementResponseTopicName;
    private String stockIncrementTopicName;
    private String stockIncrementResponseTopicName;
}
