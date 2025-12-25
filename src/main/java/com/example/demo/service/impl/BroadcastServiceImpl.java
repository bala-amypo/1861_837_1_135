public class BroadcastServiceImpl implements BroadcastService {

    private final EventUpdateRepository updateRepo;
    private final SubscriptionRepository subRepo;
    private final BroadcastLogRepository logRepo;

    public BroadcastServiceImpl(EventUpdateRepository u, SubscriptionRepository s, BroadcastLogRepository b) {
        this.updateRepo = u;
        this.subRepo = s;
        this.logRepo = b;
    }

    public void broadcastUpdate(Long updateId) {
        EventUpdate update = updateRepo.findById(updateId)
                .orElseThrow(() -> new ResourceNotFoundException("Update not found"));

        List<Subscription> subs = subRepo.findByEventId(update.getEvent().getId());
        for (Subscription s : subs) {
            BroadcastLog log = new BroadcastLog();
            log.setEventUpdate(update);
            log.setSubscriber(s.getUser());
            logRepo.save(log);
        }
    }

    public void recordDelivery(Long updateId, Long uid, boolean success) {
        for (BroadcastLog log : logRepo.findByEventUpdateId(updateId)) {
            if (log.getSubscriber().getId().equals(uid)) {
                log.setDeliveryStatus(success ? DeliveryStatus.SENT : DeliveryStatus.FAILED);
                logRepo.save(log);
            }
        }
    }

    public List<BroadcastLog> getLogsForUpdate(Long id) {
        return logRepo.findByEventUpdateId(id);
    }
}
