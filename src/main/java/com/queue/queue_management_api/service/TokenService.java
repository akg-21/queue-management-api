package com.queue.queue_management_api.service;

import com.queue.queue_management_api.DTO.TokenResponse;
import com.queue.queue_management_api.DTO.TokenStatusResponse;
import com.queue.queue_management_api.constants.QueueStatus;
import com.queue.queue_management_api.model.Queue;
import com.queue.queue_management_api.repository.QueueRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TokenService {

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private QueueSettingsService queueSettingsService;

    @Transactional
    public TokenResponse bookToken(String patientName) {

        // Check if queue is open
        if (!queueSettingsService.isQueueOpen()) {
            throw new RuntimeException("Queue is closed.");
        }

        // Lock and get today's last token
        Queue lastQueue = queueRepository.findLastTokenForUpdate().orElse(null);

        int nextToken = 1;

        if (lastQueue != null) {
            nextToken = lastQueue.getTokenNumber() + 1;
        }

        // Create new queue entry
        Queue queue = new Queue();
        queue.setPatientName(patientName);
        queue.setTokenNumber(nextToken);
        queue.setStatus(QueueStatus.WAITING);
        queue.setQueueDate(LocalDate.now());
        queue.setQueuePosition(nextToken);
        // Save to database
        Queue savedQueue = queueRepository.save(queue);

        // Find current serving token
        Queue currentServing = queueRepository.findByStatus(QueueStatus.SERVING).orElse(null);

        int currentlyServing = currentServing != null
                ? currentServing.getTokenNumber()
                : 0;

        int estimatedWaitTime = (savedQueue.getQueuePosition() - currentlyServing)
                * queueSettingsService.getSettings().getMinutesPerPatient();

        // Build response
        TokenResponse response = new TokenResponse();
        response.setTokenNumber(savedQueue.getTokenNumber());
        response.setCurrentlyServing(currentlyServing);
        response.setEstimatedWaitTime(estimatedWaitTime);
        response.setStatus("WAITING");

        return response;
    }

    public TokenStatusResponse getToken(Integer tokenNumber) {

        Queue queue = queueRepository
                .findByQueueDateAndTokenNumber(LocalDate.now(), tokenNumber)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        TokenStatusResponse response = new TokenStatusResponse();
        response.setTokenNumber(queue.getTokenNumber());
        response.setPatientName(queue.getPatientName());
        response.setDate(queue.getQueueDate());
        response.setQueuePosition(queue.getQueuePosition());

        return response;
    }
}