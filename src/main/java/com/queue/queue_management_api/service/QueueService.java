package com.queue.queue_management_api.service;

import com.queue.queue_management_api.DTO.ActionResponse;
import com.queue.queue_management_api.DTO.NextPatientResponse;
import com.queue.queue_management_api.DTO.QueueDisplayResponse;
import com.queue.queue_management_api.DTO.QueuePatientDto;
import com.queue.queue_management_api.constants.QueueStatus;
import com.queue.queue_management_api.model.Queue;
import com.queue.queue_management_api.repository.QueueRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class QueueService {
    @Autowired
    private QueueRepository queueRepository;
    @Autowired
    private QueueSettingsService queueSettingsService;

    public QueueDisplayResponse getQueueDisplay() {

        QueueDisplayResponse response = new QueueDisplayResponse();
        response.setQueueOpen(queueSettingsService.isQueueOpen());

        Queue serving = queueRepository
                .findFirstByStatus(QueueStatus.SERVING)
                .orElse(null);

        if (serving != null) {
            QueuePatientDto current = new QueuePatientDto();
            current.setTokenNumber(serving.getTokenNumber());
            current.setPatientName(serving.getPatientName());
            current.setQueuePosition(serving.getQueuePosition());

            response.setCurrentlyServing(current);
        }
        List<Queue> waitingQueues = queueRepository
                .findByStatusOrderByQueuePosition(QueueStatus.WAITING);

        List<QueuePatientDto> waitingPatients = new ArrayList<>();

        for (Queue queue : waitingQueues) {

            QueuePatientDto patient = new QueuePatientDto();
            patient.setTokenNumber(queue.getTokenNumber());
            patient.setPatientName(queue.getPatientName());
            patient.setQueuePosition(queue.getQueuePosition());

            waitingPatients.add(patient);
        }

        response.setWaitingPatients(waitingPatients);

        return response;
    }

    @Transactional
    public NextPatientResponse callNextPatient() {

        // Check if a patient is currently being served
        boolean isServing = queueRepository.existsByStatus(QueueStatus.SERVING);
        if (isServing) {
            throw new RuntimeException("Only one patient can be in service at a time. Please complete or skip the current patient first.");
        }

        // Get next waiting patient
        Queue nextPatient = queueRepository
                .findFirstByStatusOrderByQueuePosition(QueueStatus.WAITING)
                .orElseThrow(() -> new RuntimeException("No waiting patients."));

        // Mark as serving
        nextPatient.setStatus(QueueStatus.SERVING);
        queueRepository.save(nextPatient);

        // Build response
        NextPatientResponse response = new NextPatientResponse();
        response.setMessage("Next patient called successfully.");
        response.setTokenNumber(nextPatient.getTokenNumber());
        response.setPatientName(nextPatient.getPatientName());

        return response;
    }

    @Transactional
    public ActionResponse completeCurrentPatient() {

        Queue currentPatient = queueRepository
                .findFirstByStatus(QueueStatus.SERVING)
                .orElseThrow(() ->
                        new RuntimeException("No patient is currently being served."));

        currentPatient.setStatus(QueueStatus.COMPLETED);

        queueRepository.save(currentPatient);

        ActionResponse response = new ActionResponse();
        response.setMessage("Current patient completed successfully.");

        return response;
    }

    @Transactional
    public ActionResponse skipCurrentPatient() {

        Queue currentPatient = queueRepository
                .findFirstByStatus(QueueStatus.SERVING)
                .orElseThrow(() ->
                        new RuntimeException("No patient is currently being served."));

        LocalDate today = LocalDate.now();
        Queue lastQueue = queueRepository
                .findTopByQueueDateOrderByQueuePositionDesc(today)
                .orElse(null);

        int newPosition = 1;
        if (lastQueue != null) {
            newPosition = lastQueue.getQueuePosition() + 1;
        }

        currentPatient.setQueuePosition(newPosition);
        currentPatient.setStatus(QueueStatus.WAITING);

        queueRepository.save(currentPatient);

        ActionResponse response = new ActionResponse();
        response.setMessage("Current patient skipped successfully.");

        return response;
    }
}
