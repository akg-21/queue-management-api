package com.queue.queue_management_api.DTO;

public class TokenResponse {
    private Integer tokenNumber;
    private Integer currentlyServing;
    private Integer estimatedWaitTime;
    private String status;

    public Integer getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(Integer tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public Integer getCurrentlyServing() {
        return currentlyServing;
    }

    public void setCurrentlyServing(Integer currentlyServing) {
        this.currentlyServing = currentlyServing;
    }

    public Integer getEstimatedWaitTime() {
        return estimatedWaitTime;
    }

    public void setEstimatedWaitTime(Integer estimatedWaitTime) {
        this.estimatedWaitTime = estimatedWaitTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}