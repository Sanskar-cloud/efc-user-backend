package com.example.efc_user.collections;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "facilities")
public class Facility {
    @Id
    private String id;
    private String name;
    private String type; // e.g., "Hall", "Green Zone", "Cabin"
    private String capacity;
    private boolean isBooked;
    private String description;
    private List<String> pictureUrls; // URL of the facility's picture
    private LocalDateTime offlineStartTime;
    private LocalDateTime offlineEndTime;
}

