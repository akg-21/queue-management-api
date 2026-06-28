package com.queue.queue_management_api.controller;

import com.queue.queue_management_api.DTO.ActionResponse;
import com.queue.queue_management_api.DTO.NextPatientResponse;
import com.queue.queue_management_api.DTO.QueueDisplayResponse;
import com.queue.queue_management_api.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/queue")
public class QueueController {
    @Autowired
    QueueService queueService;
    @GetMapping
    public QueueDisplayResponse getQueueDisplay(){
        return queueService.getQueueDisplay();
    }
    @GetMapping("call-next")
    public NextPatientResponse callNext(){
        return queueService.callNextPatient();
    }
    @GetMapping("/complete")
    public ActionResponse completeCurrentPatient(){
        return queueService.completeCurrentPatient();
    }
    @GetMapping("/skip")
    public ActionResponse skipCurrentPatient(){
        return queueService.skipCurrentPatient();
    }

}
