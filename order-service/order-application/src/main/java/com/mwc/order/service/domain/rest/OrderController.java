package com.mwc.order.service.domain.rest;


import com.mwc.order.service.domain.dto.create.*;
import com.mwc.order.service.domain.dto.create.payment.CreatePaymentCommand;
import com.mwc.order.service.domain.dto.create.payment.CreatePaymentResponse;
import com.mwc.order.service.domain.ports.input.service.OrderApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "v1/orders", produces = "application/vnd.api.v1+json")
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

//    @PutMapping("/status")
//    public ResponseEntity<UpdateOrderStatusResponse> updateOrderStatus(@RequestBody UpdateOrderStatusCommand updateOrderStatusCommand) {
//        log.info("Updating order status: {} for order: {}", updateOrderStatusCommand.getStatus(), updateOrderStatusCommand.getOrderId());
//        UpdateOrderStatusResponse updateOrderStatusResponse = orderApplicationService.updateOrderStatus(updateOrderStatusCommand);
//        return ResponseEntity.ok(updateOrderStatusResponse);
//    }

}
