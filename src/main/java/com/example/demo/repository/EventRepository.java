package com.example.demo.repository;

import com.example.demo.entity.Event;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository {

    Optional<Event> findById(Long id);

    List<Event> findByIsActiveTrue();

    List<Event> findByIsActiveTrueAndCategory(String category);

    List<Event> findByIsActiveTrueAndLocationContainingIgnoreCase(String location);

    Event save(Event event);

    void delete(Event event);
}
