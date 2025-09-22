package com.example.efc_user.payloads;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {

    private List<OrderItemDto> items;
    private String Ordertype;
    private String paymentMethod;
    private AddressDto addressDto;
}
