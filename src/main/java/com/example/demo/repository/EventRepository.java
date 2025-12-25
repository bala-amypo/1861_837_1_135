package com.example.demo.repository;

import com.example.demo.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByIsActiveTrue();
    List<Event> findByIsActiveTrueAndCategory(String category);
    List<Event> findByIsActiveTrueAndLocationContainingIgnoreCase(String location);
}
