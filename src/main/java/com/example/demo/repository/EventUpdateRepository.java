package com.example.demo.repository;

import com.example.demo.entity.EventUpdate;
import java.util.List;
import java.util.Optional;

public interface EventUpdateRepository {

    Optional<EventUpdate> findById(Long id);

    List<EventUpdate> findByEventIdOrderByTimestampAsc(Long eventId);
}
