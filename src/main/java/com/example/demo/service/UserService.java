package com.example.demo.service;

import com.example.demo.entity.User;
import java.util.*;

public interface UserService {
    User register(User user);
    User findByEmail(String email);

    // 👇 REQUIRED BY UserController
    Optional<User> findById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, User user);
}
