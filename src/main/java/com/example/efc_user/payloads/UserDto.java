package com.example.efc_user.payloads;

import lombok.Data;

import java.util.List;
@Data
public class UserDto {

    private String id;
    private String name;
    private String email;
    private String phone;
    private AddressDto defaultAddress; // Default Address
    private List<String> orderIds; // List of Order IDs

    // Getters and Sette
}

