package com.example.demo.repository;

import com.example.demo.entity.*;
import java.util.*;

public interface EventUpdateRepository {
    Optional<EventUpdate> findById(Long id);
    List<EventUpdate> findByEventIdOrderByTimestampAsc(Long eventId);
}
