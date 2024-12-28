package com.mwc.order.service.domain;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.mwc.domain.valueobject.PaymentId;
import com.mwc.domain.valueobject.PaymentStatus;
import com.mwc.domain.valueobject.PaymentMethod;
import com.mwc.order.service.domain.dto.create.CreateOrderCommand;
import com.mwc.order.service.domain.dto.create.UpdateOrderStatusCommand;
import com.mwc.order.service.domain.dto.create.payment.CreatePaymentCommand;
import com.mwc.order.service.domain.dto.create.payment.CreatePaymentResponse;
import com.mwc.order.service.domain.entity.*;
import com.mwc.order.service.domain.event.OrderApprovedEvent;
import com.mwc.order.service.domain.event.OrderCreatedEvent;
import com.mwc.order.service.domain.exception.OrderDomainException;
import com.mwc.order.service.domain.mapper.OrderDataMapper;
import com.mwc.order.service.domain.ports.output.message.publisher.OrderApprovedDeductedStockRequestMessagePublisher;
import com.mwc.order.service.domain.ports.output.message.publisher.OrderCreatedMessagePublisher;
import com.mwc.order.service.domain.ports.output.message.publisher.OrderStatusUpdatedMessagePublisher;
import com.mwc.order.service.domain.ports.output.repository.*;
import com.mwc.order.service.domain.valueobject.OrderItemId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final PaymentRepository paymentRepository;
    private final AdminRepository adminRepository;


    private final OrderDomainService orderDomainService;
    private final OrderCreatedMessagePublisher orderCreatedEventDomainEventPublisher;
    private final OrderStatusUpdatedMessagePublisher orderStatusUpdatedMessagePublisher;
    private final OrderApprovedDeductedStockRequestMessagePublisher orderApprovedDeductedStockRequestMessagePublisher;

    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    private static final String BUCKET_NAME = "pay-file";


    private final AtomicLong orderItemIdGenerator = new AtomicLong();

    public OrderCreateHelper(OrderDataMapper orderDataMapper,
                             CustomerRepository customerRepository,
                             ProductRepository productRepository,
                             WarehouseRepository warehouseRepository,
                             PaymentRepository paymentRepository,
                             OrderRepository orderRepository,
                             AdminRepository adminRepository,
                             OrderDomainService orderDomainService,
                             OrderStatusUpdatedMessagePublisher orderStatusUpdatedMessagePublisher,
                             OrderApprovedDeductedStockRequestMessagePublisher orderApprovedDeductedStockRequestMessagePublisher,
                             OrderCreatedMessagePublisher orderCreatedEventDomainEventPublisher
    ) {
        this.orderDataMapper = orderDataMapper;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.orderRepository = orderRepository;
        this.orderDomainService = orderDomainService;
        this.paymentRepository = paymentRepository;
        this.adminRepository = adminRepository;
        this.orderCreatedEventDomainEventPublisher = orderCreatedEventDomainEventPublisher;
        this.orderStatusUpdatedMessagePublisher = orderStatusUpdatedMessagePublisher;
        this.orderApprovedDeductedStockRequestMessagePublisher = orderApprovedDeductedStockRequestMessagePublisher;
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


    private Payment uploadFileToGCS(CreatePaymentCommand createPaymentCommand, Order order) throws IOException {
        MultipartFile paymentProofFile = createPaymentCommand.getPaymentProofFile();

        // Generate unique filename
        String fileName = UUID.randomUUID().toString() + "-" + paymentProofFile.getOriginalFilename();

        // Upload file to GCS
        BlobInfo blobInfo = storage.create(
                BlobInfo.newBuilder(BUCKET_NAME, fileName).build(),
                paymentProofFile.getInputStream()
        );

        // Log success
        log.info("Payment proof file uploaded successfully to GCS: {}", fileName);

        return Payment.builder()
                .paymentId(new PaymentId(UUID.randomUUID()))
                .orderId(order.getId())
                .amount(order.getPrice())
                .proofUrl(String.format("https://storage.googleapis.com/%s/%s", BUCKET_NAME, fileName))
                .status(PaymentStatus.PENDING_VERIFICATION)
                .paymentMethod(PaymentMethod.TRANSFER_UPLOAD)
                .build();
    }

    @Transactional
    public CreatePaymentResponse uploadPayment(CreatePaymentCommand createPaymentCommand) {
        try {
            // Get order and update payment status
            Order order = orderRepository.findById(createPaymentCommand.getOrderId())
                    .orElseThrow(() -> new OrderDomainException("Order not found for id: " + createPaymentCommand.getOrderId()));

            // Upload file to GCS
            Payment payment = uploadFileToGCS(createPaymentCommand, order);
            // Save payment to repository
            paymentRepository.save(payment);

            return orderDataMapper.paymentToCreatePaymentResponse(payment);
        } catch (IOException e) {
            log.error("Error uploading payment proof file to GCS: {}", e.getMessage());
            throw new OrderDomainException("Failed to upload payment proof file to GCS", e);
        }
    }

    public OrderApprovedEvent approveOrder(UpdateOrderStatusCommand updateOrderStatusCommand) {
        Order order = findOrder(updateOrderStatusCommand);
        Admin admin = getAdmin(updateOrderStatusCommand.getAdminId());

        return orderDomainService.approveOrder(order, updateOrderStatusCommand.getOrderStatus(), admin, orderApprovedDeductedStockRequestMessagePublisher);
    }

    private Order findOrder(UpdateOrderStatusCommand updateOrderStatusCommand) {

        return orderRepository.findById(updateOrderStatusCommand.getOrderId())
                .orElseThrow(() -> new OrderDomainException("Order not found for id: " + updateOrderStatusCommand.getOrderId()));
    }

    private Admin getAdmin(UUID id) {
        return adminRepository.findAdminById(id)
                .orElseThrow(() -> new OrderDomainException("Admin not found for id: " + id));
    }


}