package com.racingquiz.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AdminUserDto {
    private Integer id;
    private String username;
    private LocalDateTime createdAt;
}
