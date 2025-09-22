package com.example.efc_user.payloads;

import lombok.Data;
import nonapi.io.github.classgraph.json.Id;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class BookingDto {

    private String id;
    private UserDto userId;
    private FacilityDto facilityId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<OrderItemDto> items;
    private String status;
}
