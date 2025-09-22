package com.example.efc_user.services;



import com.example.efc_user.payloads.DeliveryBoyDto;
import com.example.efc_user.payloads.RegisterRequest;
import com.example.efc_user.payloads.UserDto;
import com.example.efc_user.response.UserResponse;

import java.util.UUID;

public interface UserService {
    UserDto registerNewUser(RegisterRequest registerRequest);
   DeliveryBoyDto registerNewDeliveryBoy(RegisterRequest registerRequest);


    UserDto createUser(UserDto user);

    UserDto updateUser(UserDto user, String userId);

    UserDto getUserById(String userId);

    UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

    void deleteUser(String userId);
}
