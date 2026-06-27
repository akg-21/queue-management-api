package com.queue.queue_management_api.controller;

import com.queue.queue_management_api.model.QueueSettings;
import com.queue.queue_management_api.service.QueueSettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings")
public class SettingsController {

    private final QueueSettingsService queueSettingsService;

    public SettingsController(QueueSettingsService queueSettingsService) {
        this.queueSettingsService = queueSettingsService;
    }

    /**
     * Get Queue Settings
     */
    @GetMapping
    public ResponseEntity<QueueSettings> getSettings() {
        return ResponseEntity.ok(queueSettingsService.getSettings());
    }

//    /**
//     * Update Queue Settings
//     */
//    @PutMapping
//    public ResponseEntity<QueueSettings> updateSettings(
//            @RequestBody QueueSettings queueSettings) {
//
//        QueueSettings updatedSettings = settingsService.updateSettings(queueSettings);
//        return ResponseEntity.ok(updatedSettings);
//    }
}