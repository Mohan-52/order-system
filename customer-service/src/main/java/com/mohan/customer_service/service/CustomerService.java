package com.mohan.customer_service.service;

import com.mohan.customer_service.dto.CustomerRequest;
import com.mohan.customer_service.dto.CustomerResponse;
import com.mohan.customer_service.entity.Customer;
import com.mohan.customer_service.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public CustomerResponse createCustomer(CustomerRequest request) {
        Customer customer = Customer.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .userId(request.getUserId())
                .build();

        return mapToResponse(repository.save(customer));
    }

    public List<CustomerResponse> getAllCustomers() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CustomerResponse getCustomerById(Long id) {
        return repository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
    }

    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());

        return mapToResponse(repository.save(customer));
    }

    public void deleteCustomer(Long id) {
        repository.deleteById(id);
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .build();
    }
}
