package com.example.demo.service.impl;

import com.example.demo.entity.EventUpdate;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.EventUpdateRepository;
import com.example.demo.service.EventUpdateService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventUpdateServiceImpl implements EventUpdateService {

    private final EventUpdateRepository eventUpdateRepository;

    // ✅ CONSTRUCTOR REQUIRED BY TESTS
    public EventUpdateServiceImpl(EventUpdateRepository eventUpdateRepository,
                                  EventRepository eventRepository) {
        this.eventUpdateRepository = eventUpdateRepository;
    }

    // ✅ CONSTRUCTOR FOR SPRING
    public EventUpdateServiceImpl(EventUpdateRepository eventUpdateRepository) {
        this.eventUpdateRepository = eventUpdateRepository;
    }

    @Override
    public List<EventUpdate> getUpdatesForEvent(Long eventId) {
        return eventUpdateRepository.findByEventIdOrderByTimestampAsc(eventId);
    }
}
