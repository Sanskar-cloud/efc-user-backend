package com.example.efc_user.collections;

import com.example.efc_user.payloads.HallDto;
import com.example.efc_user.payloads.OrderItem;
import com.example.efc_user.payloads.UserDto;
import lombok.Data;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
@Data
@Document(collection = "bookings")
public class Bookings {
    @Id
    private String id;
    private String userId;
    private String facilityId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<OrderItem> items;
    private String status;

    // Getters and Setters
}
