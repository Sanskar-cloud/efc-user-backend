package com.example.efc_user.config;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
//
//@Service
//public class BackblazeAuthService {
//
////    @Value("${backblaze.b2.applicationKeyId}")
////    private String applicationKeyId;
////
////    @Value("${backblaze.b2.applicationKey}")
////    private String applicationKey;
//
//    private final RestTemplate restTemplate;
//    private String apiUrl;
//    private String authorizationToken;
//
//    public BackblazeAuthService(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
//
//    public void authenticate() {
//        String authUrl = "https://api.backblazeb2.com/b2api/v2/b2_authorize_account";
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBasicAuth("73cd5ba19353", "005c9f23b07abc81627ddd23a3e0e8da22f438844");
//
//        HttpEntity<Void> entity = new HttpEntity<>(headers);
//        ResponseEntity<BackblazeConfig.BackblazeAuthResponse> response = restTemplate.exchange(
//                authUrl,
//                HttpMethod.GET,
//                entity,
//                BackblazeConfig.BackblazeAuthResponse.class
//        );
//
//        BackblazeConfig.BackblazeAuthResponse authResponse = response.getBody();
//        if (authResponse == null || authResponse.getApiUrl() == null || authResponse.getAuthorizationToken() == null) {
//            throw new IllegalStateException("Failed to authenticate with Backblaze B2.");
//        }
//
//        this.apiUrl = authResponse.getApiUrl();
//        this.authorizationToken = authResponse.getAuthorizationToken();
//    }
//
//    public String getApiUrl() {
//        if (apiUrl == null) {
//            authenticate();
//        }
//        return apiUrl;
//    }
//
//    public String getAuthorizationToken() {
//        if (authorizationToken == null) {
//            authenticate();
//        }
//        return authorizationToken;
//    }
//}

