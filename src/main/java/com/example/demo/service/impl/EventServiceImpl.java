package com.example.demo.service.impl;

import com.example.demo.entity.Event;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EventService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Event createEvent(Event event) {
        User publisher = userRepository.findById(event.getPublisher().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (publisher.getRole() != Role.ADMIN && publisher.getRole() != Role.PUBLISHER) {
            throw new BadRequestException("Only PUBLISHER or ADMIN can create events");
        }
        event.setPublisher(publisher); // Ensure managed entity
        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Long id, Event updated) {
        Event existing = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setLocation(updated.getLocation());
        existing.setCategory(updated.getCategory());
        // Trigger update timestamp
        existing.onUpdate();
        
        return eventRepository.save(existing);
    }

    @Override
    public Event getById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    }

    @Override
    public List<Event> getActiveEvents() {
        return eventRepository.findByIsActiveTrue();
    }

    @Override
    public void deactivateEvent(Long id) {
        Event existing = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        existing.setActive(false);
        eventRepository.save(existing);
    }

    @Override
    public Event getEventById(Long id) {
        return getById(id);
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
}