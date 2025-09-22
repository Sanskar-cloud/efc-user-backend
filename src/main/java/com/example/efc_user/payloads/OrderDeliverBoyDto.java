package com.example.efc_user.payloads;

import com.example.efc_user.collections.Address;
import com.example.efc_user.collections.DeliveryBoy;
import com.example.efc_user.collections.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class OrderDeliverBoyDto {
    private String id;
    private UserDto user;
    private List<OrderItem> items;
    private AddressDto deliveryAddress;
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
