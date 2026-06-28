package com.queue.queue_management_api.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenStatusResponse {

    private Integer tokenNumber;
    private String patientName;
    private Integer queuePosition;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate date;
    private Integer currentlyServing;
    private Integer estimatedWaitTime;
    private String status;
}