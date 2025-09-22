package com.example.efc_user.payloads;

import lombok.Data;

@Data
public class UpdateEmailRequest {
    private String newEmail;
    private String oldEmailOtp;
    private String newEmailOtp;
}

