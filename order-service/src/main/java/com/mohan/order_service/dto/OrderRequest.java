package com.mohan.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private Long customerId;
    private List<OrderLineItem> items;

    @Data
    public static class OrderLineItem {
        private Long productId;
        private int quantity;
    }
}
