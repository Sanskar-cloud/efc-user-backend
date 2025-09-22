package com.example.efc_user.collections;




import lombok.Data;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Document(collection = "transaction")
public class RazorpayTransaction {

    @Id

    private String id;

    private String razorpayOrderId;
    private String failureReason;


    private String userId;

    private double amount;



    private TransactionStatus status; // PENDING, SUCCESS, FAILED



    private LocalDateTime createdAt = LocalDateTime.now();


}


