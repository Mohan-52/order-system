package com.mohan.order_service.service;

import com.mohan.order_service.client.CustomerClient;
import com.mohan.order_service.client.InventoryClient;
import com.mohan.order_service.dto.CustomerResponse;
import com.mohan.order_service.dto.InventoryResponse;
import com.mohan.order_service.dto.OrderRequest;
import com.mohan.order_service.dto.OrderResponse;
import com.mohan.order_service.entity.Order;
import com.mohan.order_service.entity.OrderItem;
import com.mohan.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final InventoryClient inventoryClient;
    private final CustomerClient customerClient;

    public OrderService(OrderRepository repository, InventoryClient inventoryClient, CustomerClient customerClient) {
        this.repository = repository;
        this.inventoryClient = inventoryClient;
        this.customerClient = customerClient;
    }

    public OrderResponse placeOrder(OrderRequest request) {
        // 1. Validate customer
        customerClient.getCustomer(request.getCustomerId());

        // 2. Check stock
        for (OrderRequest.OrderLineItem item : request.getItems()) {
            InventoryResponse stock = inventoryClient.checkStock(item.getProductId());
            if (!stock.isInStock()) {
                throw new RuntimeException("Product " + item.getProductId() + " is out of stock");
            }
        }

        // 3. Save order
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setOrderDate(LocalDateTime.now());
        List<OrderItem> items = request.getItems().stream().map(i ->
                OrderItem.builder()
                        .productId(i.getProductId())
                        .quantity(i.getQuantity())
                        .order(order)
                        .build()
        ).toList();
        order.setItems(items);
        Order saved = repository.save(order);

        // 4. Reduce inventory
        for (OrderItem item : saved.getItems()) {
            inventoryClient.reduceStock(item.getProductId(), item.getQuantity());
        }

        return new OrderResponse(saved.getId(), saved.getOrderDate());
    }

}

