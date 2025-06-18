package com.mohan.inventory_service.controller;

import com.mohan.inventory_service.dto.InventoryResponse;
import com.mohan.inventory_service.entity.Inventory;
import com.mohan.inventory_service.service.InventoryService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<InventoryResponse> checkStock(@PathVariable Long productId) {
        return ResponseEntity.ok(service.checkStock(productId));
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<Inventory> addInventory(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int quantity) {
        return new ResponseEntity<>(service.addInventory(productId, quantity), HttpStatus.CREATED);
    }

    @PutMapping("/reduce/{productId}")
    public ResponseEntity<Void> reduceInventory(
            @PathVariable Long productId,
            @RequestParam int quantity) {
        service.reduceStock(productId, quantity);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/add/{productId}")
    public ResponseEntity<Void> addQuantity(
            @PathVariable Long productId,
            @RequestParam int quantity) {
        service.addQuantity(productId, quantity);
        return ResponseEntity.noContent().build();
    }

}
