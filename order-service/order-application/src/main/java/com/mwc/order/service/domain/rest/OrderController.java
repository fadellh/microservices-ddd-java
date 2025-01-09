package com.mwc.order.service.domain.rest;


import com.mwc.domain.valueobject.OrderStatus;
import com.mwc.order.service.domain.dto.create.*;
import com.mwc.order.service.domain.dto.create.payment.CreatePaymentCommand;
import com.mwc.order.service.domain.dto.create.payment.CreatePaymentResponse;
import com.mwc.order.service.domain.dto.retrieve.order.RetrieveOrderDetailQueryResponse;
import com.mwc.order.service.domain.dto.retrieve.order.RetrieveOrderQuery;
import com.mwc.order.service.domain.dto.retrieve.order.RetrieveOrderQueryResponse;
import com.mwc.order.service.domain.ports.input.service.OrderApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "v1/orders", produces = "application/vnd.api.v1+json")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderCommand createOrderCommand) {
        log.info("Creating order for customer: {}", createOrderCommand.getCustomerId());
        CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
        return ResponseEntity.ok(createOrderResponse);
    }

    @GetMapping
    public ResponseEntity<List<RetrieveOrderQueryResponse>> getOrders(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(value = "order_number", required = false) UUID orderNumber,
            @RequestParam(value = "order_start_date", required = false) String orderStartDate,
            @RequestParam(value = "order_end_date", required = false) String orderEndDate
    ) {
        log.info("Retrieving orders for user: {}", userId);
        // Build the query object including optional parameters
        RetrieveOrderQuery orderListQuery = RetrieveOrderQuery.builder()
                .customerId(userId)
                .orderNumber(orderNumber)
                .orderStartDate(orderStartDate)
                .orderEndDate(orderEndDate)
                .build();

        // Pass query object to the service
        List<RetrieveOrderQueryResponse> orderListQueryResponse = orderApplicationService.retrieveOrders(orderListQuery);
        return ResponseEntity.ok(orderListQueryResponse);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<RetrieveOrderDetailQueryResponse> getOrder(@PathVariable UUID orderId) {
        log.info("Retrieving order: {}", orderId);
        RetrieveOrderDetailQueryResponse retrieveOrderQueryResponse = orderApplicationService.retrieveOrder(orderId);
        return ResponseEntity.ok(retrieveOrderQueryResponse);
    }

    @PostMapping("/preview")
    public ResponseEntity<PreviewOrderResponse> previewOrder(@RequestBody PreviewOrderCommand previewOrderCommand) {
        log.info("Previewing order for customer: {}", previewOrderCommand.getCustomerId());
        PreviewOrderResponse previewOrderResponse = orderApplicationService.previewOrder(previewOrderCommand);
        return ResponseEntity.ok(previewOrderResponse);
    }

    @PostMapping("/payment")
    public ResponseEntity<CreatePaymentResponse> createPayment(@RequestParam("orderId") UUID orderId, @RequestParam("paymentProofFile") MultipartFile paymentProofFile) {
        log.info("Uploading payment proof file: {} for order: {}", paymentProofFile.getOriginalFilename(), orderId);
        CreatePaymentCommand createPaymentCommand = CreatePaymentCommand.builder()
                .orderId(orderId)
                .paymentProofFile(paymentProofFile)
                .build();
        CreatePaymentResponse createPaymentResponse = orderApplicationService.createPayment(createPaymentCommand);
        return ResponseEntity.ok(createPaymentResponse);
    }

    @PostMapping("/{orderId}/approve")
    public ResponseEntity<UpdateOrderStatusResponse> approveOrder(
            @PathVariable("orderId") UUID orderId,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        log.info("Approving order: {} for user: {}", orderId, userId);
        UpdateOrderStatusCommand updateOrderStatusCommand = UpdateOrderStatusCommand.builder()
                .orderId(orderId)
                .adminId(userId)
                .orderStatus(OrderStatus.APPROVED)
                .build();
        UpdateOrderStatusResponse updateOrderStatusResponse = orderApplicationService.approveOrder(updateOrderStatusCommand);
        return ResponseEntity.ok(updateOrderStatusResponse);
    }

}
