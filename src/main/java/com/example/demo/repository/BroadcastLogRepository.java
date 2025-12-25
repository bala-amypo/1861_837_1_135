public interface BroadcastLogRepository extends JpaRepository<BroadcastLog, Long> {
    List<BroadcastLog> findByEventUpdateId(Long updateId);
}
