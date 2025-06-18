package com.mohan.product_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @Min(value = 0, message = "Price must be positive")
    private double price;
}
