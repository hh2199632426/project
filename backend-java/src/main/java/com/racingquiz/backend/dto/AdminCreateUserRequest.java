package com.racingquiz.backend.dto;

import lombok.Data;

@Data
public class AdminCreateUserRequest {
    private String username;
    private String password;
}
