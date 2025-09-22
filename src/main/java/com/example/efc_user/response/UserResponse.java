package com.example.efc_user.response;

import com.example.efc_user.payloads.UserDto;
import lombok.Data;

import java.util.List;

@Data
public class UserResponse {
    private List<UserDto> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean lastPage;
}
