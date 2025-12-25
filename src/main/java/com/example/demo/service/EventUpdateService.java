package com.example.demo.service;

import com.example.demo.entity.EventUpdate;
import java.util.List;
import java.util.Optional;

public interface EventUpdateService {
    EventUpdate publishUpdate(EventUpdate update);
    List<EventUpdate> getUpdatesForEvent(Long eventId);
    Optional<EventUpdate> getUpdateById(Long id);
    List<EventUpdate> getAllUpdates();
}