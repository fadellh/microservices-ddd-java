package com.mwc.user.service.domain;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.ZoneId;

@Slf4j
@RestController
@RequestMapping("v1/users")
//@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final CustomerJpaRepository customerJpaRepository;

    public UserController(CustomerJpaRepository customerJpaRepository) {
        this.customerJpaRepository = customerJpaRepository;
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyCustomerResponse> verifyCustomer(@RequestBody VerifyCustomerCommand verifyCustomerCommand) {
        log.info("Verifying user with email: {}", verifyCustomerCommand.getEmail());

        // Attempt to find the user by their email
        CustomerEntity customer = customerJpaRepository.findByEmail(verifyCustomerCommand.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Customer not found with email: " + verifyCustomerCommand.getEmail()));

        // Convert the LocalDateTime (if you're using that) to epoch milliseconds
        long dateJoined = customer.getCreatedAt() == null
                ? System.currentTimeMillis()
                : customer.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        // Build and return the response using default avatar, default banner, etc.
        VerifyCustomerResponse response =  VerifyCustomerResponse.builder()
                .fullname(customer.getFullname())
                .email(customer.getEmail())
                .address("")  // or you can map from entity if it exists
                .dateJoined(dateJoined)
                .build();

        return ResponseEntity.ok(response);

    }

    @PostMapping("/create")
    public ResponseEntity<CreateCustomerResponse> createCustomer(
           @RequestBody CreateCustomerCommand createCommand) {

        log.info("Creating user with email: {}", createCommand.getEmail());

        // Check if email already exists
        if (customerJpaRepository.findByEmail(createCommand.getEmail()).isPresent()) {
            CustomerEntity existingCustomer = customerJpaRepository.findByEmail(createCommand.getEmail()).get();
            return ResponseEntity.ok(CreateCustomerResponse.builder()
                    .id(existingCustomer.getId().toString())
                    .fullname(existingCustomer.getFullname())
                    .email(existingCustomer.getEmail())
                    .address("")
                    .dateJoined(System.currentTimeMillis())
                    .build());
        }

        CustomerEntity newCustomer = CustomerEntity.builder()
                .email(createCommand.getEmail())
                .fullname(createCommand.getFullname())
                .build();

        CustomerEntity savedCustomer = customerJpaRepository.save(newCustomer);

        return ResponseEntity.ok(CreateCustomerResponse.builder()
                .id(savedCustomer.getId().toString())
                .fullname(savedCustomer.getFullname())
                .email(savedCustomer.getEmail())
                .address("")
                .dateJoined(System.currentTimeMillis())
                .build());
    }
}
