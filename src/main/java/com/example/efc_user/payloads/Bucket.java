package com.example.efc_user.payloads;

import lombok.Data;

@Data
public  class Bucket {
    private String bucketId;
    private String bucketName;

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}