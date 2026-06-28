package com.queue.queue_management_api.repository;

import com.queue.queue_management_api.model.DailyCounter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface DailyCounterRepository
        extends JpaRepository<DailyCounter, LocalDate> {

}