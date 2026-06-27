package com.queue.queue_management_api.service;

import com.queue.queue_management_api.model.QueueSettings;
import com.queue.queue_management_api.repository.QueueSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class QueueSettingsService {

    @Autowired
    private QueueSettingsRepository queueSettingsRepository;

    public QueueSettings getSettings() {
        return queueSettingsRepository.findById(1)
                .orElseGet(() -> {

                    QueueSettings settings = new QueueSettings();
                    settings.setMinutesPerPatient(5);
                    settings.setQueueStatus(true);
                    settings.setCreatedAt(LocalDateTime.now());
                    settings.setUpdatedAt(LocalDateTime.now());
                    return queueSettingsRepository.save(settings);
                });
    }
}