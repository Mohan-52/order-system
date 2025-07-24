package com.mohan.auth_service.service;

import com.mohan.auth_service.client.CustomerClient;
import com.mohan.auth_service.dto.AuthRequest;
import com.mohan.auth_service.dto.CustomerRequest;
import com.mohan.auth_service.dto.RegisterRequest;
import com.mohan.auth_service.dto.RegisterResponse;
import com.mohan.auth_service.entity.User;
import com.mohan.auth_service.repository.UserRepository;
import com.mohan.auth_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CustomerClient customerClient;

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());

        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

       User savedUser= userRepository.save(user);

        // If the role is CUSTOMER, call customer-service
        if ("CUSTOMER".equalsIgnoreCase(user.getRole())) {
            CustomerRequest customerRequest = new CustomerRequest(savedUser.getName(), savedUser.getEmail(),savedUser.getPhone(), savedUser.getId());
            customerClient.createCustomer(customerRequest);
        }

        return savedUser;
    }

    public String login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return jwtUtil.generateToken(user.getEmail(),user.getRole());
    }
}
