package com.example.efc_user.services.impl;

import com.example.efc_user.config.BackblazeConfig;
import com.example.efc_user.payloads.BackblazeFileResponse;
import com.example.efc_user.payloads.BackblazeUploadUrlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class BackblazeB2Service {

    private static final Logger log = LoggerFactory.getLogger(BackblazeB2Service.class);
    private final RestTemplate restTemplate;
    private final Map<String, String> bucketIdMap;
    private final BackblazeConfig.BackblazeAuthResponse authResponse;

    public BackblazeB2Service(RestTemplate restTemplate, Map<String, String> bucketIdMap, BackblazeConfig.BackblazeAuthResponse authResponse) {
        this.restTemplate = restTemplate;
        this.bucketIdMap = bucketIdMap;
        this.authResponse = authResponse;
    }

    public String uploadFile(String bucketName, MultipartFile file) {
        if (!bucketIdMap.containsKey(bucketName)) {
            throw new IllegalArgumentException("Bucket not found: " + bucketName);
        }

        String bucketId = bucketIdMap.get(bucketName);
        try {
            // Step 1: Get Upload URL
            String getUploadUrlEndpoint = authResponse.getApiUrl() + "/b2api/v2/b2_get_upload_url";

            HttpHeaders getUploadUrlHeaders = new HttpHeaders();
            getUploadUrlHeaders.set("Authorization", authResponse.getAuthorizationToken());
            getUploadUrlHeaders.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> getUploadUrlPayload = new HashMap<>();
            getUploadUrlPayload.put("bucketId", bucketId);

            HttpEntity<Map<String, String>> getUploadUrlRequest = new HttpEntity<>(getUploadUrlPayload, getUploadUrlHeaders);

            ResponseEntity<BackblazeUploadUrlResponse> getUploadUrlResponse = restTemplate.exchange(
                    getUploadUrlEndpoint,
                    HttpMethod.POST,
                    getUploadUrlRequest,
                    BackblazeUploadUrlResponse.class
            );

            if (getUploadUrlResponse.getBody() == null || getUploadUrlResponse.getBody().getUploadUrl() == null) {
                throw new IllegalStateException("Failed to retrieve upload URL from Backblaze.");
            }

            String uploadUrl = getUploadUrlResponse.getBody().getUploadUrl();
            String uploadAuthorizationToken = getUploadUrlResponse.getBody().getAuthorizationToken();

            // Step 2: Upload File
            HttpHeaders uploadFileHeaders = new HttpHeaders();
            uploadFileHeaders.set("Authorization", uploadAuthorizationToken);

            // Ensure the file name is URL-encoded
            String encodedFileName = URLEncoder.encode(file.getOriginalFilename(), StandardCharsets.UTF_8)
                    .replace("+", "%20"); // Replace '+' with '%20' for spaces

            uploadFileHeaders.set("X-Bz-File-Name", encodedFileName);
            uploadFileHeaders.set("Content-Type", file.getContentType());
            uploadFileHeaders.set("X-Bz-Content-Sha1", "do_not_verify"); // Optionally replace with a real SHA1 hash

            byte[] fileBytes = file.getInputStream().readAllBytes();
            HttpEntity<byte[]> uploadFileRequest = new HttpEntity<>(fileBytes, uploadFileHeaders);

            ResponseEntity<BackblazeFileResponse> uploadFileResponse = restTemplate.exchange(
                    uploadUrl,
                    HttpMethod.POST,
                    uploadFileRequest,
                    BackblazeFileResponse.class
            );

            if (uploadFileResponse.getStatusCode().is2xxSuccessful() && uploadFileResponse.getBody() != null) {
                // Step 3: Generate Authorized URL for Private Bucket
                String getDownloadAuthEndpoint = authResponse.getApiUrl() + "/b2api/v2/b2_get_download_authorization";

                HttpHeaders downloadAuthHeaders = new HttpHeaders();
                downloadAuthHeaders.set("Authorization", authResponse.getAuthorizationToken());
                downloadAuthHeaders.setContentType(MediaType.APPLICATION_JSON);

                Map<String, Object> downloadAuthPayload = new HashMap<>();
                downloadAuthPayload.put("bucketId", bucketId);
                downloadAuthPayload.put("fileNamePrefix", encodedFileName);
                downloadAuthPayload.put("validDurationInSeconds", 86400 * 7); // Valid for 7 days
                // Valid for 10 minutes

                HttpEntity<Map<String, Object>> downloadAuthRequest = new HttpEntity<>(downloadAuthPayload, downloadAuthHeaders);

                ResponseEntity<Map> downloadAuthResponse = restTemplate.exchange(
                        getDownloadAuthEndpoint,
                        HttpMethod.POST,
                        downloadAuthRequest,
                        Map.class
                );

                if (downloadAuthResponse.getStatusCode().is2xxSuccessful() && downloadAuthResponse.getBody() != null) {
                    String authorizationToken = (String) downloadAuthResponse.getBody().get("authorizationToken");

                    // Construct the authorized URL
                    String baseUrl = authResponse.getDownloadUrl();
                    log.info(authResponse.getAuthorizationToken()+"ml,698536885");// e.g., https://f<file-number>.backblazeb2.com
                    String authorizedFileUrl = baseUrl + "/file/" + bucketName + "/" + encodedFileName + "?Authorization=" + authorizationToken;
                    log.info("File uploaded successfully. Authorized URL: {}", authorizedFileUrl);
                    return authorizedFileUrl;
                } else {
                    throw new IllegalStateException("Failed to generate download authorization.");
                }
            } else {
                throw new IllegalStateException("File upload failed with status: " + uploadFileResponse.getStatusCode());
            }
        } catch (IOException e) {
            log.error("Error occurred during file upload: ", e);
            throw new RuntimeException("Failed to upload file to Backblaze B2.", e);
        }
    }



}
