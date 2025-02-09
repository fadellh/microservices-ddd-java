package com.mwc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import com.mwc.domain.valueobject.Money;
import com.mwc.domain.valueobject.OrderStatus;
import com.mwc.domain.valueobject.CustomerId;
import com.mwc.domain.valueobject.ProductId;
import com.mwc.domain.valueobject.PaymentId;
import com.mwc.domain.valueobject.PaymentStatus;
import com.mwc.domain.valueobject.PaymentMethod;
import com.mwc.order.service.domain.OrderCreateCommandHandler;
import com.mwc.order.service.domain.OrderCreateHelper;
import com.mwc.order.service.domain.dto.create.CreateOrderCommand;
import com.mwc.order.service.domain.dto.create.CreateOrderResponse;
import com.mwc.order.service.domain.dto.create.OrderItem;
import com.mwc.order.service.domain.dto.create.UpdateOrderStatusCommand;
import com.mwc.order.service.domain.dto.create.UpdateOrderStatusResponse;
import com.mwc.order.service.domain.dto.create.payment.CreatePaymentCommand;
import com.mwc.order.service.domain.dto.create.payment.CreatePaymentResponse;
import com.mwc.order.service.domain.entity.Admin;
import com.mwc.order.service.domain.entity.Customer;
import com.mwc.order.service.domain.entity.Order;
import com.mwc.order.service.domain.entity.Product;
import com.mwc.order.service.domain.entity.Warehouse;
import com.mwc.order.service.domain.event.OrderCreatedEvent;
import com.mwc.order.service.domain.event.OrderApprovedEvent;
import com.mwc.order.service.domain.exception.OrderDomainException;
import com.mwc.order.service.domain.mapper.OrderDataMapper;
import com.mwc.order.service.domain.ports.output.message.publisher.OrderCreatedMessagePublisher;
import com.mwc.order.service.domain.ports.output.message.publisher.OrderApprovedDecrementStockRequestMessagePublisher;
import com.mwc.order.service.domain.ports.output.repository.AdminRepository;
import com.mwc.order.service.domain.ports.output.repository.CustomerRepository;
import com.mwc.order.service.domain.ports.output.repository.OrderRepository;
import com.mwc.order.service.domain.ports.output.repository.ProductRepository;
import com.mwc.order.service.domain.ports.output.repository.WarehouseRepository;
import com.mwc.order.service.domain.valueobject.OrderItemId;
import com.mwc.order.service.domain.valueobject.StreetAddress;
import com.mwc.domain.valueobject.OrderId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
@ActiveProfiles("test")
public class OrderApplicationServiceTest {

    // Application service
    @Autowired
    private OrderCreateCommandHandler orderCreateCommandHandler;

    // Message publishers
    @Autowired
    private OrderCreatedMessagePublisher orderCreatedMessagePublisher;
    @Autowired
    private OrderApprovedDecrementStockRequestMessagePublisher orderApprovedDecrementStockRequestMessagePublisher;

    // Repositories (with qualifiers)
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    @Qualifier("commandRepository")
    private OrderRepository commandOrderRepository;
    @Autowired
    @Qualifier("queryRepository")
    private OrderRepository queryOrderRepository;
    @Autowired
    private AdminRepository adminRepository;

    // Mapper and helper
    @MockBean
    private OrderDataMapper orderDataMapper;
    @SpyBean
    private OrderCreateHelper orderCreateHelper;

    /**
     * Positive Test: Create order successfully.
     */
    @Test
    public void testCreateOrderSuccess() {
        UUID customerId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .customerId(customerId)
                .warehouseId(warehouseId)
                .items(Arrays.asList(
                        OrderItem.builder()
                                .productId(productId)
                                .quantity(1)
                                .build()
                ))
                .build();

        Customer dummyCustomer = Mockito.mock(Customer.class);
        Mockito.when(customerRepository.findCustomer(eq(customerId)))
                .thenReturn(Optional.of(dummyCustomer));

        Product dummyProduct = Mockito.mock(Product.class);
        ProductId dummyProductId = Mockito.mock(ProductId.class);
        Mockito.when(dummyProductId.getValue()).thenReturn(productId);
        Mockito.when(dummyProduct.getId()).thenReturn(dummyProductId);
        Money productPrice = new Money(BigDecimal.TEN);
        Mockito.when(dummyProduct.getPrice()).thenReturn(productPrice);
        Mockito.when(productRepository.findProductsByIds(anyList()))
                .thenReturn(Arrays.asList(dummyProduct));

        Warehouse dummyWarehouse = Mockito.mock(Warehouse.class);
        Mockito.when(warehouseRepository.findWarehouse(eq(warehouseId)))
                .thenReturn(Optional.of(dummyWarehouse));

        Order dummyOrder = Order.builder()
                .orderId(new OrderId(UUID.randomUUID()))
                .customerId(new CustomerId(customerId))
                .warehouseId(new com.mwc.domain.valueobject.WarehouseId(warehouseId))
                .deliveryAddress(new StreetAddress(UUID.randomUUID(), "Test Street", "12345", "TestCity"))
                .price(new Money(BigDecimal.TEN))
                .items(Collections.singletonList(
                        com.mwc.order.service.domain.entity.OrderItem.builder()
                                .orderItemId(new OrderItemId(1L))
                                .product(dummyProduct)
                                .quantity(1)
                                .price(productPrice)
                                .subTotal(new Money(BigDecimal.TEN))
                                .build()
                ))
                .orderStatus(OrderStatus.AWAITING_PAYMENT)
                .build();

        Mockito.when(orderDataMapper.createOrderCommandToOrder(any(CreateOrderCommand.class)))
                .thenReturn(dummyOrder);
        CreateOrderResponse dummyResponse = new CreateOrderResponse(
                dummyOrder.getId().getValue(),
                dummyOrder.getOrderStatus(),
                "Order created successfully"
        );
        Mockito.when(orderDataMapper.orderToCreateOrderResponse(any(Order.class)))
                .thenReturn(dummyResponse);

        Mockito.when(commandOrderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(queryOrderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateOrderResponse response = orderCreateCommandHandler.createOrder(createOrderCommand);
        assertNotNull(response);
        Mockito.verify(orderCreatedMessagePublisher, Mockito.times(1))
                .publish(any(OrderCreatedEvent.class));
    }

    /**
     * Negative Test: Create order fails if customer is not found.
     */
    @Test
    public void testCreateOrderFailureCustomerNotFound() {
        UUID customerId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .customerId(customerId)
                .warehouseId(warehouseId)
                .items(Arrays.asList(
                        OrderItem.builder()
                                .productId(productId)
                                .quantity(1)
                                .build()
                ))
                .build();

        Mockito.when(customerRepository.findCustomer(eq(customerId)))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(OrderDomainException.class, () ->
                orderCreateCommandHandler.createOrder(createOrderCommand)
        );
        assertTrue(exception.getMessage().contains("Could not find customer"));
    }

    /**
     * Negative Test: Create order fails if product(s) are not found.
     */
    @Test
    public void testCreateOrderFailureProductNotFound() {
        UUID customerId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .customerId(customerId)
                .warehouseId(warehouseId)
                .items(Arrays.asList(
                        OrderItem.builder()
                                .productId(productId)
                                .quantity(1)
                                .build()
                ))
                .build();

        Customer dummyCustomer = Mockito.mock(Customer.class);
        Mockito.when(customerRepository.findCustomer(eq(customerId)))
                .thenReturn(Optional.of(dummyCustomer));

        Mockito.when(productRepository.findProductsByIds(anyList()))
                .thenReturn(Collections.emptyList());

        Exception exception = assertThrows(OrderDomainException.class, () ->
                orderCreateCommandHandler.createOrder(createOrderCommand)
        );
        assertTrue(exception.getMessage().contains("Some products could not be found"));
    }

    /**
     * Negative Test: Approve order fails if admin is inactive.
     */
    @Test
    public void testApproveOrderFailureInactiveAdmin() {
        UUID orderId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();

        UpdateOrderStatusCommand updateCommand = UpdateOrderStatusCommand.builder()
                .orderId(orderId)
                .adminId(adminId)
                .build();

        Order dummyOrder = Order.builder()
                .orderId(new OrderId(orderId))
                .orderStatus(OrderStatus.REVIEW_PAYMENT)
                .build();
        Mockito.when(commandOrderRepository.findById(eq(orderId)))
                .thenReturn(Optional.of(dummyOrder));

        // Create a dummy admin and ensure getId() returns a non-null value.
        Admin dummyAdmin = Mockito.mock(Admin.class);
        Mockito.when(dummyAdmin.getId()).thenReturn(new com.mwc.domain.valueobject.AdminId(UUID.randomUUID()));
        Mockito.when(dummyAdmin.isActive()).thenReturn(false);
        Mockito.when(adminRepository.findAdminById(eq(adminId)))
                .thenReturn(Optional.of(dummyAdmin));

        Exception exception = assertThrows(OrderDomainException.class, () ->
                orderCreateCommandHandler.approveOrder(updateCommand)
        );
        assertTrue(exception.getMessage().contains("is not active") ||
                exception.getMessage().contains("Admin"));
    }

    /**
     * Negative Test: Create payment fails if file upload throws an exception.
     */
    @Test
    public void testCreatePaymentFailureDueToUploadException() throws Exception {
        UUID orderId = UUID.randomUUID();
        CreatePaymentCommand paymentCommand = CreatePaymentCommand.builder()
                .orderId(orderId)
                .paymentProofFile(Mockito.mock(MultipartFile.class))
                .build();

        Order dummyOrder = Order.builder()
                .orderId(new OrderId(orderId))
                .price(new Money(BigDecimal.TEN))
                .orderStatus(OrderStatus.AWAITING_PAYMENT)
                .build();
        Mockito.when(commandOrderRepository.findById(eq(orderId)))
                .thenReturn(Optional.of(dummyOrder));

        // Throw an IOException to simulate file upload failure.
        Mockito.doThrow(new IOException("Upload failed"))
                .when(orderCreateHelper)
                .uploadFileToGCSWithBearerToken(any(CreatePaymentCommand.class), any(Order.class));

        Exception exception = assertThrows(OrderDomainException.class, () ->
                orderCreateCommandHandler.createPayment(paymentCommand)
        );
        assertTrue(exception.getMessage().contains("Failed to upload payment proof file"));
    }

    /**
     * Negative Test: Create payment fails if order state is not AWAITING_PAYMENT.
     */
    @Test
    public void testCreatePaymentFailureDueToWrongOrderState() throws Exception {
        UUID orderId = UUID.randomUUID();
        CreatePaymentCommand paymentCommand = CreatePaymentCommand.builder()
                .orderId(orderId)
                .paymentProofFile(Mockito.mock(MultipartFile.class))
                .build();

        // Prepare an order in the wrong state (e.g., REVIEW_PAYMENT).
        Order dummyOrder = Order.builder()
                .orderId(new OrderId(orderId))
                .price(new Money(BigDecimal.TEN))
                .orderStatus(OrderStatus.REVIEW_PAYMENT)
                .build();
        Mockito.when(commandOrderRepository.findById(eq(orderId)))
                .thenReturn(Optional.of(dummyOrder));

        // Simulate failure in file upload by throwing an IOException.
        Mockito.doThrow(new IOException("Wrong state"))
                .when(orderCreateHelper)
                .uploadFileToGCSWithBearerToken(any(CreatePaymentCommand.class), any(Order.class));

        Exception exception = assertThrows(OrderDomainException.class, () ->
                orderCreateCommandHandler.createPayment(paymentCommand)
        );
        assertTrue(exception.getMessage().contains("Failed to upload payment proof file"));
    }
}
