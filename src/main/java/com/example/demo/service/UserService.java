package com.example.demo.service;

import com.example.demo.entity.User;

import java.util.List;

public interface UserService {

    User register(User user);

    User findByEmail(String email);

    User findById(Long id);

    List<User> getAllUsers();
}