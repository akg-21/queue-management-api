package com.queue.queue_management_api.repository;

import com.queue.queue_management_api.model.QueueSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueSettingsRepository extends JpaRepository<QueueSettings, Integer> {
}