    package com.queue.queue_management_api.controller;

    import com.queue.queue_management_api.DTO.MinutesPerPatientRequest;
    import com.queue.queue_management_api.DTO.QueueStatusRequest;
    import com.queue.queue_management_api.DTO.SettingsResponse;
    import com.queue.queue_management_api.model.QueueSettings;
    import com.queue.queue_management_api.service.QueueSettingsService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/settings")
    public class SettingsController {

        @Autowired
        private QueueSettingsService queueSettingsService;


        /**
         * Get Queue Settings
         */
        @GetMapping
        public ResponseEntity<SettingsResponse> getSettings() {
            QueueSettings settings = queueSettingsService.getSettings();
            SettingsResponse response = new SettingsResponse(
                    settings.getMinutesPerPatient(),
                    settings.getQueueStatus()
            );
            return ResponseEntity.ok(response);
        }

        @PutMapping("/minutes-per-patient")
        public ResponseEntity<SettingsResponse> updateMinutesPerPatient(
                @RequestBody MinutesPerPatientRequest request) {
            QueueSettings settings = queueSettingsService.updateMinutesPerPatient(request.getMinutesPerPatient());
            SettingsResponse response = new SettingsResponse(
                    settings.getMinutesPerPatient(),
                    settings.getQueueStatus()
            );
            return ResponseEntity.ok(response);
        }

        @PutMapping("/queue-status")
        public ResponseEntity<SettingsResponse> updateQueueStatus(
                @RequestBody QueueStatusRequest request) {
            QueueSettings settings = queueSettingsService.updateQueueStatus(request.getQueueStatus());
            SettingsResponse response = new SettingsResponse(
                    settings.getMinutesPerPatient(),
                    settings.getQueueStatus()
            );
            return ResponseEntity.ok(response);
        }
    }