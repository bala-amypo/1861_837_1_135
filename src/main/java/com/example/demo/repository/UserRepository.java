package com.example.demo.repository;

import com.example.demo.entity.*;
import java.util.*;

public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    User save(User user);
}
