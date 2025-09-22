package com.example.efc_user.payloads;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
    private String otp; // Add OTP field
}

