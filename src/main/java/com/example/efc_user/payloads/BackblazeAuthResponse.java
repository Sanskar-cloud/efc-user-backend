package com.example.efc_user.payloads;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class BackblazeAuthResponse {
    private String accountId;
    private String apiUrl;
    private String authorizationToken;

    // Getters and setters
}

