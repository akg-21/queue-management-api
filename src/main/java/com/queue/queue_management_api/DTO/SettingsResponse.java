package com.queue.queue_management_api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettingsResponse {
    private Integer minutesPerPatient;
    private Boolean queueStatus;
}
