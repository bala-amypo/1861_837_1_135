package com.example.demo.service.impl;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.EventService;

import java.util.List;

public class EventServiceImpl implements EventService {

    private final EventRepository eventRepo;
    private final UserRepository userRepo;

    public EventServiceImpl(EventRepository eventRepo, UserRepository userRepo) {
        this.eventRepo = eventRepo;
        this.userRepo = userRepo;
    }

    public Event createEvent(Event event) {
        User user = userRepo.findById(event.getPublisher().getId())
                .orElseThrow();

        if (user.getRole() != Role.PUBLISHER && user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only PUBLISHER or ADMIN");
        }
        return eventRepo.save(event);
    }

    public Event updateEvent(Long id, Event updated) {
        Event e = eventRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        e.setTitle(updated.getTitle());
        e.setDescription(updated.getDescription());
        e.setLocation(updated.getLocation());
        e.setCategory(updated.getCategory());
        return eventRepo.save(e);
    }

    public void deactivateEvent(Long id) {
        Event e = eventRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        e.setActive(false);
        eventRepo.save(e);
    }

    public List<Event> getActiveEvents() {
        return eventRepo.findByIsActiveTrue();
    }

    public Event getById(Long id) {
        return eventRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }
}
