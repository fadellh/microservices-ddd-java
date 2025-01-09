package com.mwc.order.service.domain;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "Order Service API",
                version = "1.0",
                description = "Order Service API"
        )
)
@EnableJpaRepositories(basePackages = { "com.mwc.order.service.dataaccess", "com.mwc.dataaccess" })
@EnableMongoRepositories(basePackages = { "com.mwc.order.service.dataaccess", "com.mwc.dataaccess" })
@EntityScan(basePackages = { "com.mwc.order.service.dataaccess", "com.mwc.dataaccess"})
@SpringBootApplication(
        scanBasePackages = {"com.mwc"},   // <-- add your Kafka configs package
        exclude = {
                org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration.class
        })
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
