package com.example.efc_user.payloads;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "order_item")
public class OrderItem {
    @Id
    private String id;
    private String itemName;


    private String itemId;
    private Integer quantity;
    private Double price;
}
