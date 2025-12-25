public class EventServiceImpl implements EventService {

    private final EventRepository eventRepo;
    private final UserRepository userRepo;

    public EventServiceImpl(EventRepository e, UserRepository u) {
        this.eventRepo = e;
        this.userRepo = u;
    }

    public Event createEvent(Event e) {
        User p = userRepo.findById(e.getPublisher().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (p.getRole() != Role.ADMIN && p.getRole() != Role.PUBLISHER) {
            throw new BadRequestException("Only PUBLISHER or ADMIN");
        }
        return eventRepo.save(e);
    }

    public Event updateEvent(Long id, Event updated) {
        Event e = eventRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        e.setTitle(updated.getTitle());
        e.setDescription(updated.getDescription());
        e.setLocation(updated.getLocation());
        e.setCategory(updated.getCategory());
        return eventRepo.save(e);
    }

    public void deactivateEvent(Long id) {
        Event e = eventRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        e.setActive(false);
        eventRepo.save(e);
    }

    public Event getById(Long id) {
        return eventRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    }

    public List<Event> getActiveEvents() {
        return eventRepo.findByIsActiveTrue();
    }
}
