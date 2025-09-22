package com.example.efc_user.response;


import com.example.efc_user.payloads.UserDto;
import lombok.Data;

@Data
public class LoginResponse {
    private String token;


    private UserDto user;

}
