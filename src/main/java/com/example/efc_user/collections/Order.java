package com.example.efc_user.collections;



import com.example.efc_user.payloads.DeliveryAddress;
import com.example.efc_user.payloads.OrderItem;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private User user;
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
    private DeliveryBoy deliveryBoy;
}

