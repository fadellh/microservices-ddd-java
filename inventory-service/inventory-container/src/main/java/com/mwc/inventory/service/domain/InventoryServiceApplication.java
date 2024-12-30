package com.mwc.inventory.service.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@EnableJpaRepositories(basePackages = { "com.mwc.inventory.service.dataaccess", "com.mwc.dataaccess" })
//@EnableMongoRepositories(basePackages = { "com.mwc.inventory.service.dataaccess", "com.mwc.dataaccess" })
@EntityScan(basePackages = { "com.mwc.inventory.service.dataaccess", "com.mwc.dataaccess"})
@SpringBootApplication(
        scanBasePackages = {"com.mwc"},   // <-- add your Kafka configs package
        exclude = {
                org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration.class
        }
        )
public class InventoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}
