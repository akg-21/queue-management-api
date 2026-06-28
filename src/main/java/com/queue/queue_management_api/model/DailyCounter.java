package com.queue.queue_management_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class DailyCounter {
    @Id
    private LocalDate queueDate;

    private Integer lastToken;
}
