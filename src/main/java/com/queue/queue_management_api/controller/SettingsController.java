package com.queue.queue_management_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SettingsController {
    @RequestMapping("/close-queue")
    public String closeQueue(){
        return "Queue Closed";
    }
}
