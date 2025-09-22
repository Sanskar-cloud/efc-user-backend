package com.example.efc_user.config;

import com.example.efc_user.payloads.BackblazeBucketRequest;
import com.example.efc_user.payloads.BackblazeBucketResponse;
import com.example.efc_user.payloads.Bucket;
import lombok.Data;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class BackblazeConfig {

    private static final Logger log = LoggerFactory.getLogger(BackblazeConfig.class);

    @Value("${b2.buckets}")
    private List<String> bucketNames;

    @Value("${b2.keyId}")
    private String applicationKeyId;

    @Value("${b2.applicationKey}")
    private String applicationKey;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public BackblazeAuthResponse backblazeAuthResponse(RestTemplate restTemplate) {
        String authUrl = "https://api.backblazeb2.com/b2api/v2/b2_authorize_account";
        String authHeader = generateAuthorizationHeader(applicationKeyId, applicationKey);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<BackblazeAuthResponse> response = restTemplate.exchange(
                    authUrl, HttpMethod.GET, requestEntity, BackblazeAuthResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Successfully authenticated with Backblaze.");
                return response.getBody();
            } else {
                throw new IllegalStateException("Failed to authenticate with Backblaze B2. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error during Backblaze authentication: ", e);
            throw new IllegalStateException("Backblaze authentication failed.", e);
        }
    }

    @Bean
    public Map<String, String> bucketIdMap(RestTemplate restTemplate, BackblazeAuthResponse authResponse) {
        Map<String, String> bucketMap = new ConcurrentHashMap<>();
        String listBucketsUrl = authResponse.getApiUrl() + "/b2api/v2/b2_list_buckets";

        log.info("Fetching bucket IDs from Backblaze.");

        // Set up HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authResponse.getAuthorizationToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Iterate over bucket names to map them to IDs
        for (String bucketName : bucketNames) {
            try {
                // Prepare request payload
                Map<String, String> requestBody = Map.of("accountId", authResponse.getAccountId());
                HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

                // Make API call to fetch bucket list
                ResponseEntity<BackblazeBucketResponse> response = restTemplate.exchange(
                        listBucketsUrl, HttpMethod.POST, requestEntity, BackblazeBucketResponse.class);

                // Process API response
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    BackblazeBucketResponse bucketResponse = response.getBody();

                    log.debug("Bucket list response: {}", bucketResponse);

                    String bucketId = bucketResponse.getBuckets().stream()
                            .filter(bucket -> bucket.getBucketName().equals(bucketName))
                            .findFirst()
                            .map(Bucket::getBucketId)
                            .orElseThrow(() -> new IllegalStateException("Bucket not found: " + bucketName));

                    bucketMap.put(bucketName, bucketId);
                    log.info("Mapped bucket '{}' to ID '{}'.", bucketName, bucketId);
                } else {
                    throw new IllegalStateException("Invalid response while fetching buckets: " + response);
                }
            } catch (Exception e) {
                log.error("Error processing bucket '{}': ", bucketName, e);
                throw new IllegalStateException("Failed to retrieve bucket: " + bucketName, e);
            }
        }

        log.info("Successfully retrieved all bucket IDs.");
        return bucketMap;
    }


    public static String generateAuthorizationHeader(String applicationKeyId, String applicationKey) {
        String combined = applicationKeyId + ":" + applicationKey;
        return "Basic " + Base64.getEncoder().encodeToString(combined.getBytes(StandardCharsets.UTF_8));
    }

    @Data
    @Getter
    public static class BackblazeAuthResponse {
        private String accountId;
        private String downloadUrl;
        private String apiUrl;
        private String authorizationToken;
    }

    public String getBucketName(String type) {
        Map<String, String> typeToBucketMap = Map.of(
                "hall", "my-hall-bucket",
                "green-zone", "my-green-zone-bucket",
                "cabin", "my-cabin-bucket"
        );
        return typeToBucketMap.getOrDefault(type, "default-bucket");
    }
}
