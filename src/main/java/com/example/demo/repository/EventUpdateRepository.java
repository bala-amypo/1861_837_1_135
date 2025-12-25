public interface EventUpdateRepository extends JpaRepository<EventUpdate, Long> {
    List<EventUpdate> findByEventIdOrderByTimestampAsc(Long eventId);
}
