    package com.queue.queue_management_api.controller;

    import com.queue.queue_management_api.DTO.MinutesPerPatientRequest;
    import com.queue.queue_management_api.DTO.QueueStatusRequest;
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
        public ResponseEntity<QueueSettings> getSettings() {
            return ResponseEntity.ok(queueSettingsService.getSettings());
        }

        @PutMapping("/minutes-per-patient")
        public ResponseEntity<QueueSettings> updateMinutesPerPatient(
                @RequestBody MinutesPerPatientRequest request) {

            return ResponseEntity.ok(
                    queueSettingsService.updateMinutesPerPatient(request.getMinutesPerPatient()));
        }

        @PutMapping("/queue-status")
        public ResponseEntity<QueueSettings> updateQueueStatus(
                @RequestBody QueueStatusRequest request) {

            return ResponseEntity.ok(
                    queueSettingsService.updateQueueStatus(request.getQueueStatus()));
        }
    }