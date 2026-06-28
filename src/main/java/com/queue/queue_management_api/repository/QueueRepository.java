package com.queue.queue_management_api.repository;

import com.queue.queue_management_api.model.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QueueRepository extends JpaRepository<Queue, Long> {

    // Get all queues for today
    List<Queue> findByQueueDate(LocalDate queueDate);

    // Find current serving patient
    Optional<Queue> findFirstByStatus(Integer status);

    // Get all waiting patients
    List<Queue> findByStatusOrderByQueuePosition(Integer status);

    // Check if a serving patient exists
    boolean existsByStatus(Integer status);

    // Find token by date and token number
    Optional<Queue> findByQueueDateAndTokenNumber(LocalDate queueDate, Integer tokenNumber);

    // Get first waiting patient
    Optional<Queue> findFirstByStatusOrderByQueuePosition(Integer status);

    // Get patient with highest queue position for today
    Optional<Queue> findTopByQueueDateOrderByQueuePositionDesc(LocalDate queueDate);

    // Count patients ahead of a specific position who are WAITING or SERVING today
    @Query("SELECT COUNT(q) FROM Queue q WHERE q.queueDate = ?1 AND q.status IN (0, 1) AND q.queuePosition < ?2")
    long countPatientsAhead(LocalDate date, Integer position);
}