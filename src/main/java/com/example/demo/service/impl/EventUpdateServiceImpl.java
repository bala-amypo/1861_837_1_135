package com.example.demo.service.impl;

import com.example.demo.entity.Event;
import com.example.demo.entity.EventUpdate;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.EventUpdateRepository;
import com.example.demo.service.BroadcastService;
import com.example.demo.service.EventUpdateService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventUpdateServiceImpl implements EventUpdateService {

    private final EventUpdateRepository updateRepository;
    private final EventRepository eventRepository;
    private final BroadcastService broadcastService;

    public EventUpdateServiceImpl(EventUpdateRepository updateRepository,
                                  EventRepository eventRepository,
                                  BroadcastService broadcastService) {
        this.updateRepository = updateRepository;
        this.eventRepository = eventRepository;
        this.broadcastService = broadcastService;
    }

    @Override
    public EventUpdate publishUpdate(EventUpdate update) {

        Event event = eventRepository.findById(update.getEvent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        update.setEvent(event);
        EventUpdate saved = updateRepository.save(update);

        broadcastService.broadcastUpdate(saved.getId());
        return saved;
    }

    @Override
    public List<EventUpdate> getUpdatesForEvent(Long eventId) {
        return updateRepository.findByEventIdOrderByPostedAtAsc(eventId);
    }

    @Override
    public EventUpdate getUpdateById(Long id) {
        return updateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Update not found"));
    }
}