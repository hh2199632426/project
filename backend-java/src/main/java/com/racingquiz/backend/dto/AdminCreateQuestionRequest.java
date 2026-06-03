package com.racingquiz.backend.dto;

import lombok.Data;

@Data
public class AdminCreateQuestionRequest {
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption;
    private Long categoryId;
    private Integer difficulty;
    private Integer isActive;
}
