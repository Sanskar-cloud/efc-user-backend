package com.example.efc_user.payloads;

public class BackblazeUploadUrlRequest {
    private final String bucketId;

    public BackblazeUploadUrlRequest(String bucketId) {
        this.bucketId = bucketId;
    }

    public String getBucketId() {
        return bucketId;
    }
}

