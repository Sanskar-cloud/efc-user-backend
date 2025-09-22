package com.example.efc_user.payloads;

import lombok.Data;

@Data
public class UpdateUserProfileRequest {
    private String name;
    private String phoneNumber;
    private String profilePicturePath;
}
