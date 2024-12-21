package com.mwc.order.service.domain.rest;

import org.springframework.web.bind.annotation.*;

@RestController
public class HealthController {
    @GetMapping("/health")
    public String healthzs() {
        return "Application is running!";
    }
}