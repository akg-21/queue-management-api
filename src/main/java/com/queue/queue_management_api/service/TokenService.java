package com.queue.queue_management_api.service;

import com.queue.queue_management_api.DTO.TokenResponse;
import com.queue.queue_management_api.DTO.TokenStatusResponse;
import com.queue.queue_management_api.constants.QueueStatus;
import com.queue.queue_management_api.model.Queue;
import com.queue.queue_management_api.repository.QueueRepository;
import com.queue.queue_management_api.repository.DailyCounterRepository;
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

    @Autowired
    private DailyCounterRepository dailyCounterRepository;

    @Transactional
    public TokenResponse bookToken(String patientName) {

        // Check if queue is open
        if (!queueSettingsService.isQueueOpen()) {
            throw new RuntimeException("Queue is closed.");
        }

        // Lock/get or create today's daily counter to prevent concurrent duplicates
        LocalDate today = LocalDate.now();
        com.queue.queue_management_api.model.DailyCounter counter = dailyCounterRepository.findByQueueDateForUpdate(today).orElse(null);
        int nextToken;
        if (counter == null) {
            counter = new com.queue.queue_management_api.model.DailyCounter();
            counter.setQueueDate(today);
            counter.setLastToken(1);
            try {
                counter = dailyCounterRepository.saveAndFlush(counter);
                nextToken = 1;
            } catch (Exception e) {
                // If another request inserted it concurrently, select it with lock
                counter = dailyCounterRepository.findByQueueDateForUpdate(today)
                        .orElseThrow(() -> new RuntimeException("Concurrent error initializing daily counter"));
                counter.setLastToken(counter.getLastToken() + 1);
                counter = dailyCounterRepository.saveAndFlush(counter);
                nextToken = counter.getLastToken();
            }
        } else {
            counter.setLastToken(counter.getLastToken() + 1);
            counter = dailyCounterRepository.saveAndFlush(counter);
            nextToken = counter.getLastToken();
        }

        // Calculate next queuePosition for today (accounts for skipped patients moving to the end)
        int nextPosition = 1;
        Queue highestPosQueue = queueRepository.findTopByQueueDateOrderByQueuePositionDesc(today).orElse(null);
        if (highestPosQueue != null) {
            nextPosition = highestPosQueue.getQueuePosition() + 1;
        }

        // Create new queue entry
        Queue queue = new Queue();
        queue.setPatientName(patientName);
        queue.setTokenNumber(nextToken);
        queue.setStatus(QueueStatus.WAITING);
        queue.setQueueDate(today);
        queue.setQueuePosition(nextPosition);
        // Save to database
        Queue savedQueue = queueRepository.save(queue);

        // Find current serving token
        Queue currentServing = queueRepository.findFirstByStatus(QueueStatus.SERVING).orElse(null);

        int currentlyServing = currentServing != null
                ? currentServing.getTokenNumber()
                : 0;

        // Calculate estimated wait time based on actual patients ahead in the queue today
        long patientsAhead = queueRepository.countPatientsAhead(today, savedQueue.getQueuePosition());
        int estimatedWaitTime = (int) patientsAhead * queueSettingsService.getSettings().getMinutesPerPatient();

        // Build response
        TokenResponse response = new TokenResponse();
        response.setTokenNumber(savedQueue.getTokenNumber());
        response.setCurrentlyServing(currentlyServing);
        response.setEstimatedWaitTime(estimatedWaitTime);
        response.setStatus("WAITING");

        return response;
    }

    public TokenStatusResponse getToken(Integer tokenNumber) {
        LocalDate today = LocalDate.now();
        Queue queue = queueRepository
                .findByQueueDateAndTokenNumber(today, tokenNumber)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        
        Queue currentServing = queueRepository.findFirstByStatus(QueueStatus.SERVING).orElse(null);
        int currentlyServingToken = currentServing != null ? currentServing.getTokenNumber() : 0;

        long patientsAhead = 0;
        if (queue.getStatus() != QueueStatus.COMPLETED) {
            patientsAhead = queueRepository.countPatientsAhead(today, queue.getQueuePosition());
        }
        int estimatedWaitTime = (int) patientsAhead * queueSettingsService.getSettings().getMinutesPerPatient();

        String statusStr = "WAITING";
        if (queue.getStatus() == QueueStatus.SERVING) {
            statusStr = "SERVING";
        } else if (queue.getStatus() == QueueStatus.COMPLETED) {
            statusStr = "COMPLETED";
        }

        TokenStatusResponse response = new TokenStatusResponse();
        response.setTokenNumber(queue.getTokenNumber());
        response.setPatientName(queue.getPatientName());
        response.setDate(queue.getQueueDate());
        response.setQueuePosition(queue.getQueuePosition());
        response.setCurrentlyServing(currentlyServingToken);
        response.setEstimatedWaitTime(estimatedWaitTime);
        response.setStatus(statusStr);

        return response;
    }
}