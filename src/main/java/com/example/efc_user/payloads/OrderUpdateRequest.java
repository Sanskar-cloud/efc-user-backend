package com.example.efc_user.payloads;

import lombok.Data;

import java.util.UUID;
@Data
public class OrderUpdateRequest {
    private String id;
    private String status;
}
