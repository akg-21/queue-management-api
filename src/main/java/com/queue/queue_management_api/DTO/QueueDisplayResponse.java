package com.queue.queue_management_api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueueDisplayResponse {

    private Boolean queueOpen;
    private QueuePatientDto currentlyServing;
    private List<QueuePatientDto> waitingPatients;

}