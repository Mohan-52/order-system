package com.mohan.order_service.client;

import com.mohan.order_service.dto.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "customer-service")
public interface CustomerClient {

    @GetMapping("/customers/{id}")
    CustomerResponse getCustomer(@PathVariable("id") Long id);
}
