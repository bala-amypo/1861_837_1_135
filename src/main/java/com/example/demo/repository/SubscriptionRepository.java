package com.example.demo.repository;

import com.example.demo.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
    Optional<Subscription> findByUserIdAndEventId(Long userId, Long eventId);
    List<Subscription> findByUserId(Long userId);
    List<Subscription> findByEventId(Long eventId);
}
