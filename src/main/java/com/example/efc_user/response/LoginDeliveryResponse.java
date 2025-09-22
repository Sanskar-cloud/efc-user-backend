package com.example.efc_user.response;

import com.example.efc_user.payloads.DeliveryBoyDto;
import com.example.efc_user.payloads.UserDto;
import lombok.Data;

@Data
public class LoginDeliveryResponse {
    private String token;


    private DeliveryBoyDto user;

}