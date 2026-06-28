package com.queue.queue_management_api.repository;

import com.queue.queue_management_api.model.DailyCounter;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyCounterRepository
        extends JpaRepository<DailyCounter, LocalDate> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM DailyCounter d WHERE d.queueDate = :date")
    Optional<DailyCounter> findByQueueDateForUpdate(@Param("date") LocalDate date);
}