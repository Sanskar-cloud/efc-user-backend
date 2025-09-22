package com.example.efc_user.payloads;

import com.example.efc_user.collections.Address;
import com.example.efc_user.collections.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class OrderDto {
    private String id;

    private List<OrderItem> items;
    private Address deliveryAddress;
    private String deliveryType;
    private String paymentMethod;
    private String paymentStatus;
    private String deliveryStatus;
    private Double totalPrice;
    private LocalDateTime orderTime;
    private LocalDateTime paymentTime;
    private LocalDateTime deliveryTime;
    private DeliveryBoyDto deliveryBoy;
}
