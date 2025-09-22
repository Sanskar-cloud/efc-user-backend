package com.example.efc_user.payloads;

import lombok.Data;

@Data
public class DeliveryAddress {
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
}
