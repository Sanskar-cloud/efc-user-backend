package com.example.efc_user.payloads;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterRequest {

    private String name;
    private String email;


    private String password;
    private String confirmPassword;
    private String phone;
//    private String otp;


}
