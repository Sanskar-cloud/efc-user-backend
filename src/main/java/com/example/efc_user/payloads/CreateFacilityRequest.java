package com.example.efc_user.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateFacilityRequest {
    private String name;
    private String type; // "Hall", "Green Zone", "Cabin"
    private String capacity;
    private String description;
//    private String pictureUrl;
}

