package com.racingquiz.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminOverviewDto {
    private Long questionCount;
    private Long userCount;
    private Long categoryCount;
}
