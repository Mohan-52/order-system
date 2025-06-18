package com.mohan.order_service.client;

import com.mohan.order_service.dto.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service")
public interface InventoryClient {
    @GetMapping("/inventory/check/{productId}")
    InventoryResponse checkStock(@PathVariable("productId") Long productId);

    @PutMapping("/inventory/reduce/{productId}")
    void reduceStock(@PathVariable("productId") Long productId,
                     @RequestParam("quantity") int quantity);
}

