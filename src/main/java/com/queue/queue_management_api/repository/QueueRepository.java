package com.queue.queue_management_api.repository;

import com.queue.queue_management_api.constants.QueueStatus;
import com.queue.queue_management_api.model.Queue;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QueueRepository extends JpaRepository<Queue, Long> {

    List<Queue> findByQueueDate(LocalDate queueDate);

    Optional<Queue> findFirstByStatus(QueueStatus status);

    List<Queue> findByStatusOrderByQueuePosition(QueueStatus status);

    boolean existsByStatus(QueueStatus status);

    Optional<Queue> findByStatus(Integer status);

    Optional<Queue> findByQueueDateAndTokenNumber(LocalDate queueDate, Integer tokenNumber);

    @Query(value = """
    SELECT *
    FROM queue
    WHERE queue_date = CURDATE()
    ORDER BY token_number DESC
    LIMIT 1
    FOR UPDATE
    """, nativeQuery = true)
    Optional<Queue> findLastTokenForUpdate();
}