package com.example.efc_user.payloads;

import lombok.Data;

@Data
public class CreateHallRequest {
    private String name;
    private int capacity;
}
