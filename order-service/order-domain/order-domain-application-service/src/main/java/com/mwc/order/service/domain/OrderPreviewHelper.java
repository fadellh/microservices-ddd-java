package com.mwc.order.service.domain;

import com.mwc.order.service.domain.dto.create.PreviewOrderCommand;
import com.mwc.order.service.domain.dto.create.PreviewOrderResponse;
import com.mwc.order.service.domain.dto.external.InventoryResponse;
import com.mwc.order.service.domain.entity.Customer;
import com.mwc.order.service.domain.entity.Order;
import com.mwc.order.service.domain.entity.Product;
import com.mwc.order.service.domain.entity.Warehouse;
import com.mwc.order.service.domain.exception.OrderDomainException;
import com.mwc.order.service.domain.mapper.OrderDataMapper;
import com.mwc.order.service.domain.ports.output.repository.CustomerRepository;
import com.mwc.order.service.domain.ports.output.repository.ProductRepository;
import com.mwc.order.service.domain.ports.output.repository.WarehouseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrderPreviewHelper {

    private final OrderDataMapper orderDataMapper;

    private final CustomerRepository customerRepository;

    private final ProductRepository productRepository;

    private final WarehouseRepository warehouseRepository;

    private final BigDecimal PRICE_PER_KM = BigDecimal.valueOf(1);


    public OrderPreviewHelper(OrderDataMapper orderDataMapper,
                              CustomerRepository customerRepository,
                              ProductRepository productRepository, WarehouseRepository warehouseRepository
    ) {
        this.orderDataMapper = orderDataMapper;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }

//    @Transactional
//    public PreviewOrderResponse previewOrder(PreviewOrderCommand previewOrderCommand) {
//        Order order = orderDataMapper.previewOrderCommandToOrder(previewOrderCommand);
//        // Perform necessary calculations and validations
//        BigDecimal totalAmount = calculateTotalAmount(order);
//        BigDecimal shippingCost = calculateShippingCost(order);
//        BigDecimal discount = calculateDiscount(order);
//        return orderDataMapper.orderToPreviewOrderResponse(order, totalAmount, shippingCost, discount);
//    }

    public void checkCustomer(UUID customerId) {
        Optional<Customer> customer = customerRepository.findCustomer(customerId);
        if (customer.isEmpty()) {
            log.warn("Could not find customer with customer id: {}", customerId);
            throw new OrderDomainException("Could not find customer with customer id: " + customer);
        }
    }

    public void checkProductStock(UUID productId) {
//        InventoryResponse response = inventoryApplicationService.checkStock(productId);
//        if (!response.isInStock()) {
//            log.warn("Product with ID {} is out of stock", productId);
//            throw new RuntimeException("Product is out of stock");
//        }
    }

    public List<Product> checkProduct(List<UUID> productIds) {
        List<Product> products = productRepository.findProductsByIds(productIds);
        if (products.size() != productIds.size()) {
            log.warn("Some products could not be found for product ids: {}", productIds);
            throw new OrderDomainException("Some products could not be found for product ids: " + productIds);
        }
        return products;
    }

    public BigDecimal calculateTotalAmount(Order order) {
        // Implement total amount calculation logic
        return BigDecimal.ZERO;
    }

    public BigDecimal calculateShippingCost(List<Warehouse> nearestWarehouses) {
        // Implement shipping cost calculation logic
        if (nearestWarehouses.isEmpty()) {
            return BigDecimal.ZERO;
        }
        double distanceInMeters = nearestWarehouses.get(0).getDistance();
        double distanceInKm = distanceInMeters / 1000.0;

        int cost = BigDecimal.valueOf(distanceInKm).multiply(PRICE_PER_KM).intValue();

        return BigDecimal.valueOf(cost);
    }

    public List<Warehouse> findNearestWarehouse(double latitude, double longitude) {
        return warehouseRepository.findNearestWarehouse(latitude, longitude);
    }


    public BigDecimal calculateDiscount(Order order) {
        // Implement discount calculation logic
        return BigDecimal.ZERO;
    }
}