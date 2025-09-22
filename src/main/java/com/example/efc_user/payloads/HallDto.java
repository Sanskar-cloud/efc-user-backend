package com.example.efc_user.payloads;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class HallDto {
    private String id;
    private String name;
    private int capacity;
    private boolean isBooked;
    private LocalDateTime offlineStartTime;
    private LocalDateTime offlineEndTime;
}
