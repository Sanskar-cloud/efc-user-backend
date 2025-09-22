package com.example.efc_user.payloads;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FacilityDto {
    private String id;
    private String name;
    private String type; // e.g., "Hall", "Green Zone", "Cabin"
    private String capacity;
    private boolean isBooked;
    private String description;
    private List<String> pictureUrls;
    private LocalDateTime offlineStartTime;
    private LocalDateTime offlineEndTime;
}

