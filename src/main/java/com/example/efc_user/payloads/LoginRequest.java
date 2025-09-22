package com.example.efc_user.payloads;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;

    private String password;


}
