package com.example.efc_user.collections;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
@Data
@Document(collection = "delivery_boy_review")
public class DeliveryBoyReview {

    @Id
    private String id;
    private String deliveryBoyId;
    private String customerId;
    private int rating;
    private String review;
    private LocalDateTime reviewDate;

}

