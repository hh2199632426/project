package com.racingquiz.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AdminCategoryDto {
    private Long id;
    private String name;
    private Long questionCount;
    private LocalDateTime createdAt;
}
