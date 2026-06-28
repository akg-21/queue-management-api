package com.queue.queue_management_api.controller;

import com.queue.queue_management_api.DTO.TokenRequest;
import com.queue.queue_management_api.DTO.TokenResponse;
import com.queue.queue_management_api.DTO.TokenStatusResponse;
import com.queue.queue_management_api.model.Queue;
import com.queue.queue_management_api.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/token")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public TokenResponse bookToken(@RequestBody TokenRequest request) {
        return tokenService.bookToken(request.getPatientName());
    }

    @GetMapping("/{tokenNumber}")
    public TokenStatusResponse getToken(@PathVariable Integer tokenNumber) {
        return tokenService.getToken(tokenNumber);
    }
}