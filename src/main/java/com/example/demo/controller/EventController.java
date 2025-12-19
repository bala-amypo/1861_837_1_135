package com.example.demo.controller;

import com.example.demo.entity.Event;
import com.example.demo.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        return eventService.createEvent(event);
    }

    @PutMapping("/{id}")
    public Event updateEvent(@PathVariable Long id,
                             @RequestBody Event event) {
        return eventService.updateEvent(id, event);
    }

    @GetMapping("/{id}")
    public Event getEvent(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @GetMapping("/active")
    public List<Event> getActiveEvents() {
        return eventService.getActiveEvents();
    }

    @PatchMapping("/{id}/deactivate")
    public Event deactivateEvent(@PathVariable Long id) {
        return eventService.deactivateEvent(id);
    }
}