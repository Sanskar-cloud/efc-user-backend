package com.example.efc_user.collections;

import lombok.Data;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@Data
@Document(collection = "halls")
public class Hall {
    @Id
    private String id;
    private String name;
    private int capacity;
    private boolean isBooked;
    private LocalDateTime offlineStartTime;
    private LocalDateTime offlineEndTime;

    // Getters and Setters
}
