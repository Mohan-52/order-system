package com.mohan.inventory_service.service;

import com.mohan.inventory_service.dto.InventoryResponse;
import com.mohan.inventory_service.entity.Inventory;
import com.mohan.inventory_service.repository.InventoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    private final InventoryRepository repository;

    public InventoryService(InventoryRepository repository) {
        this.repository = repository;
    }

    public InventoryResponse checkStock(Long productId) {
        Inventory inventory = repository.findByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found in inventory"));

        return InventoryResponse.builder()
                .productId(productId)
                .inStock(inventory.getQuantity() > 0)
                .build();
    }

    public void reduceStock(Long productId, int quantity) {
        Inventory inventory = repository.findByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (inventory.getQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        repository.save(inventory);
    }

    public void addQuantity(Long productId, int quantity) {
        Inventory inventory = repository.findByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found in inventory"));

        inventory.setQuantity(inventory.getQuantity() + quantity);
        repository.save(inventory);
    }


    public Inventory addInventory(Long productId, int quantity) {
        return repository.save(Inventory.builder()
                .productId(productId)
                .quantity(quantity)
                .build());
    }
}
