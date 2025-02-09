package com.mwc;


import com.mwc.order.service.domain.OrderDomainService;
import com.mwc.order.service.domain.OrderDomainServiceImpl;
import com.mwc.order.service.domain.ports.input.message.listener.StockDecrementResponseMessageListener;
import com.mwc.order.service.domain.ports.output.message.publisher.OrderApproveFailedMessagePublisher;
import com.mwc.order.service.domain.ports.output.message.publisher.OrderApprovedDecrementStockRequestMessagePublisher;
import com.mwc.order.service.domain.ports.output.message.publisher.OrderCreatedMessagePublisher;
import com.mwc.order.service.domain.ports.output.message.publisher.OrderStatusUpdatedMessagePublisher;
import com.mwc.order.service.domain.ports.output.repository.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = "com.mwc.order.service.domain")
public class OrderTestConfiguration {

    @Bean
    public OrderCreatedMessagePublisher orderCreatedMessagePublisher() {
        return Mockito.mock(OrderCreatedMessagePublisher.class);
    }

    @Bean
    public OrderApproveFailedMessagePublisher orderApproveFailedMessagePublisher() {
        return Mockito.mock(OrderApproveFailedMessagePublisher.class);
    }

    @Bean
    public OrderApprovedDecrementStockRequestMessagePublisher orderApprovedDecrementStockRequestMessagePublisher() {
        return Mockito.mock(OrderApprovedDecrementStockRequestMessagePublisher.class);
    }

    @Bean
    public OrderStatusUpdatedMessagePublisher orderStatusUpdatedMessagePublisher() {
        return Mockito.mock(OrderStatusUpdatedMessagePublisher.class);
    }

    @Bean
    public WarehouseRepository warehouseRepository() {
        return Mockito.mock(WarehouseRepository.class);
    }

    @Bean
    public ProductRepository productRepository() {
        return Mockito.mock(ProductRepository.class);
    }

    @Bean
    public PaymentRepository paymentRepository() {
        return Mockito.mock(PaymentRepository.class);
    }

    @Bean
    @Qualifier("commandRepository")
    public OrderRepository commandOrderRepository() {
        return Mockito.mock(OrderRepository.class);
    }

    @Bean
    @Qualifier("queryRepository")
    public OrderRepository queryOrderRepository() {
        return Mockito.mock(OrderRepository.class);
    }

    @Bean
    public CustomerRepository customerRepository() {
        return Mockito.mock(CustomerRepository.class);
    }

    @Bean
    public AdminRepository adminRepository() {
        return Mockito.mock(AdminRepository.class);
    }

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }

    @Bean
    public StockDecrementResponseMessageListener stockDecrementResponseMessageListener() {
        return Mockito.mock(StockDecrementResponseMessageListener.class);
    }

}
