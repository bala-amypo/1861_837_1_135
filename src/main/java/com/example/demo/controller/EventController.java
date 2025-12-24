package com.example.demo.controller;

import com.example.demo.dto.EventRequest;
import com.example.demo.entity.Event;
import com.example.demo.entity.User;
import com.example.demo.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/")
    @PreAuthorize("hasAnyRole('PUBLISHER', 'ADMIN')")
    public ResponseEntity<Event> createEvent(@RequestBody EventRequest request) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setCategory(request.getCategory());
        
        User publisher = new User();
        publisher.setId(request.getPublisherId());
        event.setPublisher(publisher);

        return new ResponseEntity<>(eventService.createEvent(event), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PUBLISHER', 'ADMIN')")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody EventRequest request) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setCategory(request.getCategory());
        // Publisher usually shouldn't change, or maybe it can? Requirements don't specify.
        // Service updateEvent only updates title, desc, loc, category.
        
        return ResponseEntity.ok(eventService.updateEvent(id, event));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getById(id));
    }

    @GetMapping("/")
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Event>> getActiveEvents() {
        return ResponseEntity.ok(eventService.getActiveEvents());
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('PUBLISHER', 'ADMIN')")
    public ResponseEntity<Void> deactivateEvent(@PathVariable Long id) {
        eventService.deactivateEvent(id);
        return ResponseEntity.ok().build();
    }
}