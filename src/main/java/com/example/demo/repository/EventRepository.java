package com.example.demo.repository;

import com.example.demo.entity.*;
import java.util.*;

public interface EventRepository {
    Optional<Event> findById(Long id);
    Event save(Event event);
    void delete(Event event);
    List<Event> findByIsActiveTrue();
    List<Event> findByIsActiveTrueAndCategory(String category);
    List<Event> findByIsActiveTrueAndLocationContainingIgnoreCase(String location);
}
