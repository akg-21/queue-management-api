package com.queue.queue_management_api.service;

import com.queue.queue_management_api.constants.QueueStatus;
import com.queue.queue_management_api.model.QueueSettings;
import com.queue.queue_management_api.repository.QueueRepository;
import com.queue.queue_management_api.repository.QueueSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class QueueSettingsService {

    @Autowired
    private QueueSettingsRepository queueSettingsRepository;

    @Autowired
    private QueueRepository queueRepository;

    public QueueSettings getSettings() {
        return queueSettingsRepository.findById(1)
                .orElseGet(this::createDefaultSettings);
    }

    private QueueSettings createDefaultSettings() {
        QueueSettings settings = new QueueSettings();
        settings.setMinutesPerPatient(5);
        settings.setQueueStatus(true);

        return queueSettingsRepository.save(settings);
    }

    public QueueSettings updateMinutesPerPatient(Integer minutes) {
        QueueSettings settings = getSettings();
        settings.setMinutesPerPatient(minutes);
        return queueSettingsRepository.save(settings);
    }

    public QueueSettings updateQueueStatus(Boolean status) {
        QueueSettings settings = getSettings();
        if (Boolean.FALSE.equals(status)) {
            // Check if any patient is currently being served
            boolean isServing = queueRepository.existsByStatus(QueueStatus.SERVING);
            if (isServing) {
                throw new RuntimeException("Queue cannot be closed while a patient is currently being served.");
            }
        }
        settings.setQueueStatus(status);
        return queueSettingsRepository.save(settings);
    }

    public boolean isQueueOpen() {
        return getSettings().getQueueStatus();
    }

}