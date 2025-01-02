package com.mwc.order.service.domain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "order-service")
public class OrderServiceConfigData {
    private String orderCreateTopicName;
    private String orderCreateResponseTopicName;
    private String stockDecrementRequestTopicName;
    private String stockDecrementResponseTopicName;
}