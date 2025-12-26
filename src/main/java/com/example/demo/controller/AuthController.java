package com.example.demo.controller;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Authentication endpoints")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        if (request.getRole() != null) {
            try {
                user.setRole(Role.valueOf(request.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                user.setRole(Role.SUBSCRIBER);
            }
        } else {
            user.setRole(Role.SUBSCRIBER);
        }

        User saved = userService.register(user);
        
        String token = jwtUtil.generateToken(saved.getId(), saved.getEmail(), saved.getRole().name());
        
        return new ResponseEntity<>(new AuthResponse(token, saved.getId(), saved.getEmail(), saved.getRole().name()), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(request.getEmail());  // No .orElseThrow() needed

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        return ResponseEntity.ok(new AuthResponse(token, user.getId(), user.getEmail(), user.getRole().name()));
    }
}