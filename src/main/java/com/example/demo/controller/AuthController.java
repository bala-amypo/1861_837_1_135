package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        User saved = userService.register(user);
        return jwtUtil.generateToken(
                saved.getId(),
                saved.getEmail(),
                saved.getRole().name()
        );
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        User found = userService.findByEmail(user.getEmail());
        return jwtUtil.generateToken(
                found.getId(),
                found.getEmail(),
                found.getRole().name()
        );
    }
}
