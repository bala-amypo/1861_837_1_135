package com.example.demo.repository;

import com.example.demo.entity.EventUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventUpdateRepository extends JpaRepository<EventUpdate, Long> {
    List<EventUpdate> findByEventIdOrderByTimestampAsc(Long eventId);
}
