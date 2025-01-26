package com.mwc.inventory.service.domain.rest;

import com.mwc.inventory.service.domain.dto.product.ProductItemResponse;
import com.mwc.inventory.service.domain.dto.product.ProductsResponse;
import com.mwc.inventory.service.domain.dto.transfer.TransferInventoryCommand;
import com.mwc.inventory.service.domain.dto.transfer.TransferInventoryResponse;
import com.mwc.inventory.service.domain.ports.input.message.service.InventoryApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "v1/inventory", produces = "application/vnd.api.v1+json")
@CrossOrigin(origins = "http://localhost:3000")
public class InventoryController {

    private final InventoryApplicationService inventoryApplicationService;

    public InventoryController(InventoryApplicationService inventoryApplicationService) {
        this.inventoryApplicationService = inventoryApplicationService;
    }

    @PostMapping("/{inventoryId}/transfer")
    public ResponseEntity<TransferInventoryResponse> transferInventory(
            @PathVariable UUID inventoryId,
            @RequestBody TransferInventoryCommand transferInventoryCommand) {
        log.info("Transferring inventory with id: {}", inventoryId);
        TransferInventoryResponse transferInventoryResponse = inventoryApplicationService.transferInventory(inventoryId, transferInventoryCommand);
        return ResponseEntity.ok(transferInventoryResponse);
    }

    @GetMapping("/products")
    public  ResponseEntity<ProductsResponse> getProducts() {
        log.info("Getting all products");
//        List<ProductResponse> productResponse = inventoryApplicationService.getProducts();
        ProductsResponse productResponse = ProductsResponse.builder()
                .products(inventoryApplicationService.retrieveCatalogs())
                .build();

        return ResponseEntity.ok(productResponse);

    }

}