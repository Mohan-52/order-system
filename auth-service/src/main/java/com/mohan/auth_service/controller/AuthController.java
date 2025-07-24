package com.mohan.auth_service.controller;

import com.mohan.auth_service.dto.AuthRequest;
import com.mohan.auth_service.dto.AuthResponse;
import com.mohan.auth_service.dto.RegisterRequest;
import com.mohan.auth_service.dto.RegisterResponse;
import com.mohan.auth_service.entity.User;
import com.mohan.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        User savedUser = authService.register(request);
        RegisterResponse response=new RegisterResponse(savedUser.getId(),"User registered successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        String token = authService.login(request);
        AuthResponse response=new AuthResponse(token);
        return ResponseEntity.ok(response);
    }
}
