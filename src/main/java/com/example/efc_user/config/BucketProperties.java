package com.example.efc_user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "b2.buckets")
public class BucketProperties {
    private Map<String, String> buckets;

    public String getBucketName(String type) {
        return buckets.getOrDefault(type, "default-bucket");
    }

    public Map<String, String> getBuckets() {
        return buckets;
    }

    public void setBuckets(Map<String, String> buckets) {
        this.buckets = buckets;
    }
}

