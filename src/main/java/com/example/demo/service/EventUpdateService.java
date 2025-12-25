package com.example.demo.service;

import com.example.demo.entity.EventUpdate;
import java.util.List;

public interface EventUpdateService {

    List<EventUpdate> getUpdatesForEvent(Long eventId);
}
