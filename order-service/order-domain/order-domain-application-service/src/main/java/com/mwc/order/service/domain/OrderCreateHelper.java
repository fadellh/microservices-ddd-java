package com.mwc.order.service.domain;

import com.mwc.order.service.domain.dto.create.CreateOrderCommand;
import com.mwc.order.service.domain.entity.*;
import com.mwc.order.service.domain.event.OrderCreatedEvent;
import com.mwc.order.service.domain.exception.OrderDomainException;
import com.mwc.order.service.domain.mapper.OrderDataMapper;
import com.mwc.order.service.domain.ports.output.message.publisher.OrderCreatedMessagePublisher;
import com.mwc.order.service.domain.ports.output.repository.CustomerRepository;
import com.mwc.order.service.domain.ports.output.repository.OrderRepository;
import com.mwc.order.service.domain.ports.output.repository.ProductRepository;
import com.mwc.order.service.domain.ports.output.repository.WarehouseRepository;
import com.mwc.order.service.domain.valueobject.OrderItemId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderCreateHelper {

    private final OrderDataMapper orderDataMapper;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;
    private final OrderCreatedMessagePublisher orderCreatedEventDomainEventPublisher;



    private final AtomicLong orderItemIdGenerator = new AtomicLong();

    public OrderCreateHelper(OrderDataMapper orderDataMapper,
                             CustomerRepository customerRepository,
                             ProductRepository productRepository,
                             WarehouseRepository warehouseRepository,
                             OrderRepository orderRepository,
                             OrderDomainService orderDomainService,
                             OrderCreatedMessagePublisher orderCreatedEventDomainEventPublisher
    ) {
        this.orderDataMapper = orderDataMapper;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.orderRepository = orderRepository;
        this.orderDomainService = orderDomainService;
        this.orderCreatedEventDomainEventPublisher = orderCreatedEventDomainEventPublisher;
    }


    @Transactional
    public OrderCreatedEvent persistOrder(CreateOrderCommand createOrderCommand) {
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        checkCustomer(createOrderCommand.getCustomerId());

        // Validate products and create OrderItems
        List<OrderItem> orderItems = validateAndCreateOrderItems(createOrderCommand);
        order.setItems(orderItems);

        // Check if the warehouse exists
        checkWarehouse(createOrderCommand.getWarehouseId());

        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order, orderItems,
                orderCreatedEventDomainEventPublisher);
        saveOrder(order);
        log.info("Order is created with id: {}", orderCreatedEvent.getOrder().getId().getValue());
        return orderCreatedEvent;
    }


    private Order saveOrder(Order order) {
        Order orderResult = orderRepository.save(order);
        if (orderResult == null) {
            log.error("Could not save order!");
            throw new OrderDomainException("Could not save order!");
        }
        log.info("Order is saved with id: {}", orderResult.getId().getValue());
        return orderResult;
    }

    private void checkCustomer(UUID customerId) {
        Optional<Customer> customer = customerRepository.findCustomer(customerId);
        if (customer.isEmpty()) {
            log.warn("Could not find customer with customer id: {}", customerId);
            throw new OrderDomainException("Could not find customer with customer id: " + customerId);
        }
    }

    private List<OrderItem> validateAndCreateOrderItems(CreateOrderCommand createOrderCommand) {
        List<UUID> productIds = createOrderCommand.getItems().stream()
                .map(com.mwc.order.service.domain.dto.create.OrderItem::getProductId)
                .collect(Collectors.toList());

        List<Product> products = productRepository.findProductsByIds(productIds);
        if (products.size() != productIds.size()) {
            log.warn("Some products could not be found for product ids: {}", productIds);
            throw new OrderDomainException("Some products could not be found for product ids: " + productIds);
        }

        return createOrderCommand.getItems().stream()
                .map(orderItemDto -> {
                    Product product = products.stream()
                            .filter(p -> p.getId().getValue().equals(orderItemDto.getProductId()))
                            .findFirst()
                            .orElseThrow(() -> new OrderDomainException("Product not found for id: " + orderItemDto.getProductId()));
                    return OrderItem.builder()
                            .orderItemId(new OrderItemId(orderItemIdGenerator.incrementAndGet()))
                            .product(product)
                            .quantity(orderItemDto.getQuantity())
                            .price(product.getPrice())
                            .subTotal(product.getPrice().multiply(orderItemDto.getQuantity()))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private void checkWarehouse(UUID warehouseId) {
        Optional<Warehouse> warehouse = warehouseRepository.findWarehouse(warehouseId);
        if (warehouse.isEmpty()) {
            log.warn("Could not find warehouse with warehouse id: {}", warehouseId);
            throw new OrderDomainException("Could not find warehouse with warehouse id: " + warehouseId);
        }
    }

}