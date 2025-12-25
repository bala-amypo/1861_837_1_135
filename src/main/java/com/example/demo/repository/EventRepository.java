public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByIsActiveTrue();
    List<Event> findByIsActiveTrueAndCategory(String category);
    List<Event> findByIsActiveTrueAndLocationContainingIgnoreCase(String location);
}
