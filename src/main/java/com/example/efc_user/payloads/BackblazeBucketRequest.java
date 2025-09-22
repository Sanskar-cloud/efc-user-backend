package com.example.efc_user.payloads;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class BackblazeBucketRequest {
    private final String accountId;

    public BackblazeBucketRequest(String accountId) {
        this.accountId = accountId;
    }

}

