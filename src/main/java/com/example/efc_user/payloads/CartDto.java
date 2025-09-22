package com.example.efc_user.payloads;

import com.example.efc_user.collections.Cart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
    private String userId;
    private List<OrderItemDto> items;
    private double totalPrice;

    public CartDto(Cart cart) {
        this.userId = cart.getUserId();
        this.items = cart.getItems();
        this.totalPrice = cart.getTotalPrice();
    }
}