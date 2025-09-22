package com.example.efc_user.collections;

import com.example.efc_user.payloads.OrderItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "carts")
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

    @Id
    private String id;
    private String userId;
    private List<OrderItemDto> items = new ArrayList<>();
    private double totalPrice;

    public Cart(String userId) {
        this.userId = userId;
    }

    public void addItem(OrderItemDto orderItem) {
        this.items.add(orderItem);
    }

    public void removeItem(String itemId) {
        this.items.removeIf(item -> item.getItemId().equals(itemId));
    }

    public void calculateTotalPrice() {
        this.totalPrice = items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }}

