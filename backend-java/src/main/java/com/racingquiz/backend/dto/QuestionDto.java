package com.racingquiz.backend.dto;

import java.util.List;

public record QuestionDto(
        Long id,
        String text,
        List<String> options,
        String answer,
        String category
) {
}
