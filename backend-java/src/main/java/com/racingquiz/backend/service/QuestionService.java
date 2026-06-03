package com.racingquiz.backend.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.racingquiz.backend.dto.QuestionDto;
import com.racingquiz.backend.entity.Category;
import com.racingquiz.backend.entity.Question;
import com.racingquiz.backend.mapper.CategoryMapper;
import com.racingquiz.backend.mapper.QuestionMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    private final QuestionMapper questionMapper;
    private final CategoryMapper categoryMapper;

    public QuestionService(QuestionMapper questionMapper, CategoryMapper categoryMapper) {
        this.questionMapper = questionMapper;
        this.categoryMapper = categoryMapper;
    }

    public List<QuestionDto> getQuestions(Integer count, List<Long> categoryIds) {
        int safeCount = count == null ? 50 : Math.max(1, Math.min(count, 250));
        var queryWrapper = Wrappers.<Question>lambdaQuery()
                .eq(Question::getIsActive, 1);
        
        if (categoryIds != null && !categoryIds.isEmpty()) {
            queryWrapper.in(Question::getCategoryId, categoryIds);
        }
        
        List<Question> questions = questionMapper.selectList(
                queryWrapper.last("ORDER BY RAND() LIMIT " + safeCount)
        );

        if (questions.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> foundCategoryIds = questions.stream()
                .map(Question::getCategoryId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());

        Map<Long, String> categoryNameMap = new HashMap<>();
        if (!foundCategoryIds.isEmpty()) {
            List<Category> categories = categoryMapper.selectBatchIds(foundCategoryIds);
            for (Category category : categories) {
                categoryNameMap.put(category.getId(), category.getName());
            }
        }

        return questions.stream()
                .map(question -> {
                    String categoryName = categoryNameMap.getOrDefault(question.getCategoryId(), question.getCategory());
                    if (categoryName == null || categoryName.isEmpty()) {
                        categoryName = "综合知识";
                    }
                    List<String> options = new ArrayList<>(4);
                    addIfNotBlank(options, question.getOptionA());
                    addIfNotBlank(options, question.getOptionB());
                    addIfNotBlank(options, question.getOptionC());
                    addIfNotBlank(options, question.getOptionD());
                    String answer = resolveAnswer(question);
                    if (options.size() < 2 || answer == null || answer.trim().isEmpty()) {
                        return null;
                    }
                    return new QuestionDto(
                            question.getId(),
                            question.getQuestionText(),
                            options,
                            answer,
                            categoryName
                    );
                })
                .filter(dto -> dto != null)
                .toList();
    }

    private void addIfNotBlank(List<String> list, String value) {
        if (value == null) {
            return;
        }
        String trimmed = value.trim();
        if (!trimmed.isEmpty()) {
            list.add(trimmed);
        }
    }

    private String resolveAnswer(Question question) {
        return switch (question.getCorrectOption()) {
            case "A" -> question.getOptionA();
            case "B" -> question.getOptionB();
            case "C" -> question.getOptionC();
            case "D" -> question.getOptionD();
            default -> "";
        };
    }

    public List<Category> getAllCategories() {
        return categoryMapper.selectList(null);
    }
}
