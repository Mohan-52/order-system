package com.mohan.auth_service.client;

import com.mohan.auth_service.dto.CustomerRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "customer-service")  // must match Eureka service name
public interface CustomerClient {

    @PostMapping("/customers")
    ResponseEntity<Void> createCustomer(@RequestBody CustomerRequest request);
}
