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
import com.mwc.order.service.domain.ports.output.message.publisher.OrderApprovedDecrementStockRequestMessagePublisher;
import com.mwc.order.service.domain.ports.output.message.publisher.OrderCreatedMessagePublisher;
import com.mwc.order.service.domain.ports.output.message.publisher.OrderStatusUpdatedMessagePublisher;
import com.mwc.order.service.domain.ports.output.repository.*;
import com.mwc.order.service.domain.valueobject.OrderItemId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;


import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private final OrderRepository commandOrderRepository;
    private final OrderRepository queryOrderRepository;
    private final PaymentRepository paymentRepository;
    private final AdminRepository adminRepository;


    private final OrderDomainService orderDomainService;
    private final OrderCreatedMessagePublisher orderCreatedEventDomainEventPublisher;
    private final OrderStatusUpdatedMessagePublisher orderStatusUpdatedMessagePublisher;
    private final OrderApprovedDecrementStockRequestMessagePublisher orderApprovedDeductedStockRequestMessagePublisher;

    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    @Value("${gcs.bucket.name}")
    private String BUCKET_NAME;

    @Value("${gcs.oauth.token}")
    private String OAUTH_TOKEN;


    private final AtomicLong orderItemIdGenerator = new AtomicLong();

    public OrderCreateHelper(OrderDataMapper orderDataMapper,
                             CustomerRepository customerRepository,
                             ProductRepository productRepository,
                             WarehouseRepository warehouseRepository,
                             PaymentRepository paymentRepository,
                             @Qualifier("commandRepository") OrderRepository commandOrderRepository,
                             @Qualifier("queryRepository")  OrderRepository queryOrderRepository,
                             AdminRepository adminRepository,
                             OrderDomainService orderDomainService,
                             OrderStatusUpdatedMessagePublisher orderStatusUpdatedMessagePublisher,
                             OrderApprovedDecrementStockRequestMessagePublisher orderApprovedDeductedStockRequestMessagePublisher,
                             OrderCreatedMessagePublisher orderCreatedEventDomainEventPublisher
    ) {
        this.orderDataMapper = orderDataMapper;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.commandOrderRepository = commandOrderRepository;
        this.queryOrderRepository = queryOrderRepository;
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
        order = saveOrder(order);
        log.info("Order is created with id: {}", orderCreatedEvent.getOrder().getId().getValue());
        return orderCreatedEvent;
    }


    private Order saveOrder(Order order) {
        Order orderResult = commandOrderRepository.save(order);
        if (orderResult == null) {
            log.error("Could not save order!");
            throw new OrderDomainException("Could not save order!");
        }
        log.info("Order is saved with id: {}", orderResult.getId().getValue());

        queryOrderRepository.save(order);
        log.info("Order is saved with id: {} to query repository", orderResult.getId().getValue());

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

    private Payment uploadFileToGCSWithBearerToken(CreatePaymentCommand createPaymentCommand, Order order) throws IOException {
        MultipartFile paymentProofFile = createPaymentCommand.getPaymentProofFile();

        // Generate unique filename
        String fileName = UUID.randomUUID().toString() + "-" + paymentProofFile.getOriginalFilename();

        // The GCS JSON API endpoint for direct uploads:
        // https://storage.googleapis.com/upload/storage/v1/b/BUCKET_NAME/o?uploadType=media&name=FILENAME
        String uploadUrl = String.format(
                "https://storage.googleapis.com/upload/storage/v1/b/%s/o?uploadType=media&name=%s",
                BUCKET_NAME, fileName
        );

        // Make HTTP connection
        HttpURLConnection connection = (HttpURLConnection) new URL(uploadUrl).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        // Add "Authorization: Bearer YOUR_ACCESS_TOKEN"
        connection.setRequestProperty("Authorization", "Bearer " + OAUTH_TOKEN);

        // Set the Content-Type to the file's MIME type (e.g. "image/png")
        connection.setRequestProperty("Content-Type", paymentProofFile.getContentType());

        // Send file bytes
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(paymentProofFile.getBytes());
        }

        // Check response
        int responseCode = connection.getResponseCode();
        if (responseCode == 200 || responseCode == 201) {
            log.info("Payment proof file uploaded successfully to GCS with Bearer token: {}", fileName);
        } else {
            String errorMessage = "Error uploading file to GCS. Response code: " + responseCode;
            log.error(errorMessage);
            throw new IOException(errorMessage);
        }

        // Build Payment object after successful upload
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
            Order order = commandOrderRepository.findById(createPaymentCommand.getOrderId())
                    .orElseThrow(() -> new OrderDomainException("Order not found for id: " + createPaymentCommand.getOrderId()));

            // Upload file to GCS
            Payment payment = uploadFileToGCSWithBearerToken(createPaymentCommand, order);
            orderDomainService.initReviewPayment(order);
            // Save payment to repository
            paymentRepository.save(payment);
            commandOrderRepository.save(order);
            queryOrderRepository.save(order);

            return orderDataMapper.paymentToCreatePaymentResponse(payment);
        } catch (IOException e) {
            log.error("Error uploading payment proof file to GCS: {}", e.getMessage());
            throw new OrderDomainException("Failed to upload payment proof file to GCS", e);
        }
    }

    @Transactional
    public OrderApprovedEvent approveOrder(UpdateOrderStatusCommand updateOrderStatusCommand) {
        Order order = findOrder(updateOrderStatusCommand);
        Admin admin = getAdmin(updateOrderStatusCommand.getAdminId());

        OrderApprovedEvent orderApprovedEvent = orderDomainService.initApproveOrder(order, admin, orderApprovedDeductedStockRequestMessagePublisher);
        saveOrder(order);
        log.info("Order status is approved with id: {}", orderApprovedEvent.getOrder().getId().getValue());
        return orderApprovedEvent;
    }


    private Order findOrder(UpdateOrderStatusCommand updateOrderStatusCommand) {

        return commandOrderRepository.findById(updateOrderStatusCommand.getOrderId())
                .orElseThrow(() -> new OrderDomainException("Order not found for id: " + updateOrderStatusCommand.getOrderId()));
    }

    private Admin getAdmin(UUID id) {
        return adminRepository.findAdminById(id)
                .orElseThrow(() -> new OrderDomainException("Admin not found for id: " + id));
    }


}