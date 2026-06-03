package com.racingquiz.backend.controller;

import com.racingquiz.backend.dto.QuestionDto;
import com.racingquiz.backend.entity.Category;
import com.racingquiz.backend.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class QuestionController {
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/questions")
    public List<QuestionDto> getQuestions(
            @RequestParam(required = false) Integer count,
            @RequestParam(required = false) List<Long> categoryIds
    ) {
        try {
            return questionService.getQuestions(count, categoryIds);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "题目加载失败，请重试");
        }
    }

    @GetMapping("/categories")
    public List<Category> getCategories() {
        return questionService.getAllCategories();
    }
}
