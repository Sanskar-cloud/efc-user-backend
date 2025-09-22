package com.example.efc_user.payloads;

import lombok.Data;

@Data
public class OrderFromCartRequest {
    private AddressDto deliveryAddress;  // Delivery Address
    private String deliveryType;      // e.g., "Standard", "Express"
    private String paymentMethod;     // e.g., "Credit Card", "Cash on Delivery"
}
