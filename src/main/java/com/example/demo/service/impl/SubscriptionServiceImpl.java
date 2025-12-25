public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subRepo;
    private final UserRepository userRepo;
    private final EventRepository eventRepo;

    public SubscriptionServiceImpl(SubscriptionRepository s, UserRepository u, EventRepository e) {
        this.subRepo = s;
        this.userRepo = u;
        this.eventRepo = e;
    }

    public Subscription subscribe(Long uid, Long eid) {
        if (subRepo.existsByUserIdAndEventId(uid, eid)) {
            throw new BadRequestException("Already subscribed");
        }
        User u = userRepo.findById(uid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Event e = eventRepo.findById(eid)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        Subscription s = new Subscription();
        s.setUser(u);
        s.setEvent(e);
        return subRepo.save(s);
    }

    public void unsubscribe(Long uid, Long eid) {
        Subscription s = subRepo.findByUserIdAndEventId(uid, eid)
                .orElseThrow(() -> new BadRequestException("Subscription not found"));
        subRepo.delete(s);
    }

    public boolean isSubscribed(Long uid, Long eid) {
        return subRepo.existsByUserIdAndEventId(uid, eid);
    }

    public List<Subscription> getUserSubscriptions(Long uid) {
        return subRepo.findByUserId(uid);
    }
}
