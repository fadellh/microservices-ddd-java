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
                .products(new ProductItemResponse[]{
                        ProductItemResponse.builder()
                                .id(UUID.fromString("49feb4bb-883e-4ffe-8866-b53c1ee503f2"))
                                .name("Product 5")
                                .brand("Brand 1")
                                .image("https://images-cdn.ubuy.co.id/651ae1ec6550d876a420ea3e-nike-mens-air-jordan-1-mid-chicago.jpg")
                                .price(new BigDecimal(1000000))
                                .maxQuantity(10)
                                .size("10")
                                .build(),
                        ProductItemResponse.builder()
                                .id(UUID.fromString("748d7aa7-9cd1-4e10-b9dd-3f8d4bdedf74"))
                                .name("Product 2")
                                .brand("Brand 2")
                                .image("https://www.footlocker.id/media/catalog/product/cache/f57d6f7ebc711fc328170f0ddc174b08/0/1/01-NIKE-F34KBNIK5-NIK553560132-White.jpg")
                                .price(new BigDecimal(2000000))
                                .maxQuantity(20)
                                .size("11")
                                .build()
                })
                .build();

        return ResponseEntity.ok(productResponse);

    }

}