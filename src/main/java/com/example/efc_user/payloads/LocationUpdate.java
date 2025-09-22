package com.example.efc_user.payloads;

import lombok.Data;

@Data
public class LocationUpdate {
//    private String deliveryBoyId;
    private String orderId;
    private double latitude;
    private double longitude;

    // Getters and Setters
}
