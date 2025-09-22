package com.example.efc_user.payloads;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUpdateRequest {
    private String facilityId;
    private boolean isBooked;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Getters and Setters
}
