package com.queue.queue_management_api.model;

import lombok.Data;

@Data
public class QueueSettings {
    private int minutesPerPatient;
    private int queueStatus;
}
