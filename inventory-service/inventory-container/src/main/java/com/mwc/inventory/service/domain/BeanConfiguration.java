package com.mwc.inventory.service.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

         @Bean
         public InventoryDomainService inventoryDomainService() {
             return new InventoryDomainServiceImpl();
         }
}
