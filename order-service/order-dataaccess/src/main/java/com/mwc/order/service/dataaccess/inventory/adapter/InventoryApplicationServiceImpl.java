package com.mwc.order.service.dataaccess.inventory.adapter;

import com.mwc.order.service.domain.dto.external.InventoryResponse;
import com.mwc.order.service.domain.ports.output.service.InventoryApplicationService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class InventoryApplicationServiceImpl implements InventoryApplicationService {

//    private final RestTemplate restTemplate;

//    public InventoryApplicationServiceImpl(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }

//    @Override
//    public InventoryResponse checkStock(UUID productId) {
//        String url = "https://inventory-service.com/api/stock?productId=" + productId;
//        return restTemplate.getForObject(url, InventoryResponse.class);
//    }
}
