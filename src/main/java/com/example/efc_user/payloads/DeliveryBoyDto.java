package com.example.efc_user.payloads;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class DeliveryBoyDto {
    private String name;
    private String email;// Login username
     // Login password (preferably hashed)
    private String phone;
    private LocalDateTime dateOfJoining;
    private double averageRating;
    private int totalReviews;
}
