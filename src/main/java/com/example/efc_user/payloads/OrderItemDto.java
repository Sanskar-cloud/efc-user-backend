package com.example.efc_user.payloads;

import lombok.Data;

@Data
public class OrderItemDto {
    private String itemId;
    private String itemName;

    private Integer quantity;
    private Double price;
}
