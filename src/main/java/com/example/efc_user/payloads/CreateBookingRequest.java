package com.example.efc_user.payloads;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class CreateBookingRequest {
    private String facilityId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<OrderItemDto> items;
    private String description;
}
