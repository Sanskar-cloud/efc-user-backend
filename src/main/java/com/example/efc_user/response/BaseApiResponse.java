package com.example.efc_user.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BaseApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
}

