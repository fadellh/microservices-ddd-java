package com.mwc.order.service.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = { "com.mwc.order.service.dataaccess", "com.mwc.dataaccess" })
@EntityScan(basePackages = { "com.mwc.order.service.dataaccess", "com.mwc.dataaccess"})
@SpringBootApplication(scanBasePackages = "com.mwc.order")
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
