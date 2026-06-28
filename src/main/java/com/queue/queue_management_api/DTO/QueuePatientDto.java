package com.queue.queue_management_api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueuePatientDto {

    private Integer tokenNumber;
    private String patientName;
    private Integer queuePosition;

}