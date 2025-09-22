package com.example.efc_user.payloads;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Data
@Setter
public class BackblazeBucketResponse {
    private List<Bucket> buckets;

    public void setBuckets(List<Bucket> buckets) {
        this.buckets = buckets;
    }}




