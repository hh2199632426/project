package com.racingquiz.backend.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.racingquiz.backend.dto.AdminCategoryDto;
import com.racingquiz.backend.dto.AdminCreateCategoryRequest;
import com.racingquiz.backend.dto.AdminCreateQuestionRequest;
import com.racingquiz.backend.dto.AdminCreateUserRequest;
import com.racingquiz.backend.dto.AdminOverviewDto;
import com.racingquiz.backend.dto.AdminQuestionDto;
import com.racingquiz.backend.dto.AdminUserDto;
import com.racingquiz.backend.entity.Category;
import com.racingquiz.backend.entity.Question;
import com.racingquiz.backend.entity.User;
import com.racingquiz.backend.mapper.CategoryMapper;
import com.racingquiz.backend.mapper.QuestionMapper;
import com.racingquiz.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.racingquiz.backend.dto.PageResponse;

@Service
public class AdminService {
    private final UserMapper userMapper;
    private final QuestionMapper questionMapper;
    private final CategoryMapper categoryMapper;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    public AdminService(UserMapper userMapper, QuestionMapper questionMapper, CategoryMapper categoryMapper) {
        this.userMapper = userMapper;
        this.questionMapper = questionMapper;
        this.categoryMapper = categoryMapper;
    }

    public boolean login(String username, String password) {
        return adminUsername.equals(username) && adminPassword.equals(password);
    }

    public AdminOverviewDto getOverview() {
        ensureCategoryIdsSynced();
        Long questionCount = questionMapper.selectCount(Wrappers.emptyWrapper());
        Long userCount = userMapper.selectCount(Wrappers.emptyWrapper());
        Long categoryCount = categoryMapper.selectCount(Wrappers.emptyWrapper());
        return new AdminOverviewDto(questionCount, userCount, categoryCount);
    }

    public PageResponse<AdminUserDto> getUsers(long page, long size, String username) {
        Page<User> userPage = new Page<>(page, size);
        var queryWrapper = Wrappers.<User>lambdaQuery().orderByDesc(User::getCreatedAt);
        if (username != null && !username.trim().isEmpty()) {
            queryWrapper.like(User::getUsername, username.trim());
        }
        userMapper.selectPage(userPage, queryWrapper);
        List<AdminUserDto> userDtos = userPage.getRecords().stream()
                .map(user -> new AdminUserDto(user.getId(), user.getUsername(), user.getCreatedAt()))
                .toList();
        return new PageResponse<>(userDtos, userPage.getTotal(), userPage.getCurrent(), userPage.getSize());
    }

    public AdminUserDto createUser(AdminCreateUserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("参数错误");
        }
        String username = request.getUsername() == null ? "" : request.getUsername().trim();
        String password = request.getPassword() == null ? "" : request.getPassword().trim();
        if (username.isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (password.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        Long exists = userMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
        if (exists != null && exists > 0) {
            throw new IllegalArgumentException("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setNickName(generateDefaultNickName());
        userMapper.insert(user);
        User saved = userMapper.selectById(user.getId());
        return new AdminUserDto(saved.getId(), saved.getUsername(), saved.getCreatedAt());
    }

    private String generateDefaultNickName() {
        int num = ThreadLocalRandom.current().nextInt(100, 10000);
        return "小萌新" + num;
    }

    public void deleteUser(Long userId) {
        userMapper.deleteById(userId);
    }

    public void updateUser(Long userId, AdminCreateUserRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            Long exists = userMapper.selectCount(Wrappers.<User>lambdaQuery()
                    .eq(User::getUsername, request.getUsername().trim())
                    .ne(User::getId, userId));
            if (exists != null && exists > 0) {
                throw new IllegalArgumentException("用户名已存在");
            }
            user.setUsername(request.getUsername().trim());
        }
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(request.getPassword().trim());
        }
        userMapper.updateById(user);
    }

    public PageResponse<AdminQuestionDto> getQuestions(long page, long size, Long categoryId, String questionText) {
        ensureCategoryIdsSynced();
        Page<Question> questionPage = new Page<>(page, size);
        var queryWrapper = Wrappers.<Question>lambdaQuery().orderByDesc(Question::getCreatedAt);
        if (categoryId != null) {
            queryWrapper.eq(Question::getCategoryId, categoryId);
        }
        if (questionText != null && !questionText.trim().isEmpty()) {
            queryWrapper.like(Question::getQuestionText, questionText.trim());
        }
        questionMapper.selectPage(questionPage, queryWrapper);
        List<Question> questions = questionPage.getRecords();

        Set<Long> categoryIds = questions.stream()
                .map(Question::getCategoryId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());
        Map<Long, Category> categoryById = new HashMap<>();
        if (!categoryIds.isEmpty()) {
            for (Category category : categoryMapper.selectBatchIds(categoryIds)) {
                categoryById.put(category.getId(), category);
            }
        }
        List<AdminQuestionDto> dtos = questions.stream()
                .map(question -> {
                    String categoryName = question.getCategory();
                    if (question.getCategoryId() != null) {
                        Category category = categoryById.get(question.getCategoryId());
                        if (category != null) {
                            categoryName = category.getName();
                        }
                    }
                    return new AdminQuestionDto(
                            question.getId(),
                            question.getQuestionText(),
                            categoryName,
                            question.getIsActive(),
                            question.getCreatedAt(),
                            question.getCategoryId(),
                            question.getOptionA(),
                            question.getOptionB(),
                            question.getOptionC(),
                            question.getOptionD(),
                            question.getCorrectOption()
                    );
                })
                .toList();
        return new PageResponse<>(dtos, questionPage.getTotal(), questionPage.getCurrent(), questionPage.getSize());
    }

    public AdminQuestionDto createQuestion(AdminCreateQuestionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("参数错误");
        }
        String questionText = request.getQuestionText() == null ? "" : request.getQuestionText().trim();
        String optionA = request.getOptionA() == null ? "" : request.getOptionA().trim();
        String optionB = request.getOptionB() == null ? "" : request.getOptionB().trim();
        String optionC = request.getOptionC() == null ? "" : request.getOptionC().trim();
        String optionD = request.getOptionD() == null ? "" : request.getOptionD().trim();
        String correctOption = request.getCorrectOption() == null ? "" : request.getCorrectOption().trim().toUpperCase();
        if (questionText.isEmpty()) {
            throw new IllegalArgumentException("题目不能为空");
        }
        if (optionA.isEmpty() || optionB.isEmpty()) {
            throw new IllegalArgumentException("至少需要填写选项A与选项B");
        }
        if (!optionD.isEmpty() && optionC.isEmpty()) {
            throw new IllegalArgumentException("选项请按顺序填写，不能只填D不填C");
        }
        java.util.Set<String> allowedCorrectOptions = resolveAllowedCorrectOptions(optionA, optionB, optionC, optionD);
        if (!allowedCorrectOptions.contains(correctOption)) {
            throw new IllegalArgumentException("正确选项无效");
        }
        if (request.getCategoryId() == null) {
            throw new IllegalArgumentException("请选择目录");
        }
        Category category = categoryMapper.selectById(request.getCategoryId());
        if (category == null) {
            throw new IllegalArgumentException("目录不存在");
        }

        Question question = new Question();
        question.setQuestionText(questionText);
        question.setOptionA(optionA);
        question.setOptionB(optionB);
        question.setOptionC(optionC);
        question.setOptionD(optionD);
        question.setCorrectOption(correctOption);
        question.setCategory(category.getName());
        question.setCategoryId(category.getId());
        question.setIsActive(request.getIsActive() == null ? 1 : request.getIsActive());
        questionMapper.insert(question);

        Question saved = questionMapper.selectById(question.getId());
        return new AdminQuestionDto(
                saved.getId(),
                saved.getQuestionText(),
                category.getName(),
                saved.getIsActive(),
                saved.getCreatedAt(),
                saved.getCategoryId(),
                saved.getOptionA(),
                saved.getOptionB(),
                saved.getOptionC(),
                saved.getOptionD(),
                saved.getCorrectOption()
        );
    }

    public void deleteQuestion(Long questionId) {
        questionMapper.deleteById(questionId);
    }

    public void updateQuestion(Long questionId, AdminCreateQuestionRequest request) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new IllegalArgumentException("题目不存在");
        }
        if (request.getQuestionText() != null && !request.getQuestionText().trim().isEmpty()) {
            question.setQuestionText(request.getQuestionText().trim());
        }
        if (request.getOptionA() != null) question.setOptionA(request.getOptionA().trim());
        if (request.getOptionB() != null) question.setOptionB(request.getOptionB().trim());
        if (request.getOptionC() != null) question.setOptionC(request.getOptionC().trim());
        if (request.getOptionD() != null) question.setOptionD(request.getOptionD().trim());
        if (request.getCorrectOption() != null) {
            String correctOption = request.getCorrectOption().trim().toUpperCase();
            if (!correctOption.equals("A") && !correctOption.equals("B") && !correctOption.equals("C") && !correctOption.equals("D")) {
                throw new IllegalArgumentException("正确选项必须为 A/B/C/D");
            }
            question.setCorrectOption(correctOption);
        }
        if (request.getIsActive() != null) question.setIsActive(request.getIsActive());

        if (request.getCategoryId() != null) {
            Category category = categoryMapper.selectById(request.getCategoryId());
            if (category == null) {
                throw new IllegalArgumentException("目录不存在");
            }
            question.setCategoryId(category.getId());
            question.setCategory(category.getName());
        }
        String optionA = question.getOptionA() == null ? "" : question.getOptionA().trim();
        String optionB = question.getOptionB() == null ? "" : question.getOptionB().trim();
        String optionC = question.getOptionC() == null ? "" : question.getOptionC().trim();
        String optionD = question.getOptionD() == null ? "" : question.getOptionD().trim();
        if (optionA.isEmpty() || optionB.isEmpty()) {
            throw new IllegalArgumentException("至少需要填写选项A与选项B");
        }
        if (!optionD.isEmpty() && optionC.isEmpty()) {
            throw new IllegalArgumentException("选项请按顺序填写，不能只填D不填C");
        }
        String correctOption = question.getCorrectOption() == null ? "" : question.getCorrectOption().trim().toUpperCase();
        java.util.Set<String> allowedCorrectOptions = resolveAllowedCorrectOptions(optionA, optionB, optionC, optionD);
        if (!allowedCorrectOptions.contains(correctOption)) {
            throw new IllegalArgumentException("正确选项无效");
        }
        questionMapper.updateById(question);
    }

    private java.util.Set<String> resolveAllowedCorrectOptions(String optionA, String optionB, String optionC, String optionD) {
        java.util.Set<String> allowed = new java.util.HashSet<>();
        if (optionA != null && !optionA.trim().isEmpty()) {
            allowed.add("A");
        }
        if (optionB != null && !optionB.trim().isEmpty()) {
            allowed.add("B");
        }
        if (optionC != null && !optionC.trim().isEmpty()) {
            allowed.add("C");
        }
        if (optionD != null && !optionD.trim().isEmpty()) {
            allowed.add("D");
        }
        return allowed;
    }

    public List<AdminCategoryDto> getCategories() {
        ensureCategoryIdsSynced();
        List<Map<String, Object>> countRows = questionMapper.selectMaps(
                new QueryWrapper<Question>()
                        .select("category_id", "COUNT(1) AS question_count")
                        .isNotNull("category_id")
                        .groupBy("category_id")
        );
        Map<Long, Long> questionCountByCategoryId = new HashMap<>();
        for (Map<String, Object> row : countRows) {
            Object rawId = row.get("category_id");
            Object rawCount = row.get("question_count");
            if (rawId == null) {
                continue;
            }
            Long categoryId = Long.valueOf(String.valueOf(rawId));
            Long count = rawCount == null ? 0L : Long.valueOf(String.valueOf(rawCount));
            questionCountByCategoryId.put(categoryId, count);
        }

        return categoryMapper.selectList(Wrappers.<Category>lambdaQuery().orderByDesc(Category::getCreatedAt))
                .stream()
                .map(category -> new AdminCategoryDto(
                        category.getId(),
                        category.getName(),
                        questionCountByCategoryId.getOrDefault(category.getId(), 0L),
                        category.getCreatedAt()
                ))
                .toList();
    }

    public AdminCategoryDto createCategory(AdminCreateCategoryRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("参数错误");
        }
        String name = request.getName() == null ? "" : request.getName().trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("目录名称不能为空");
        }
        Long exists = categoryMapper.selectCount(Wrappers.<Category>lambdaQuery().eq(Category::getName, name));
        if (exists != null && exists > 0) {
            throw new IllegalArgumentException("目录已存在");
        }
        Category category = new Category();
        category.setName(name);
        categoryMapper.insert(category);
        Category saved = categoryMapper.selectById(category.getId());
        return new AdminCategoryDto(saved.getId(), saved.getName(), 0L, saved.getCreatedAt());
    }

    @Transactional
    public Map<String, Object> deleteCategory(Long categoryId) {
        ensureCategoryIdsSynced();
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            return Map.of("deleted", false, "message", "目录不存在");
        }
        Long questionCount = questionMapper.selectCount(Wrappers.<Question>lambdaQuery().eq(Question::getCategoryId, categoryId));
        questionMapper.delete(Wrappers.<Question>lambdaQuery().eq(Question::getCategoryId, categoryId));
        categoryMapper.deleteById(categoryId);
        return Map.of(
                "deleted", true,
                "deletedQuestions", questionCount == null ? 0L : questionCount,
                "message", "删除成功"
        );
    }

    private void ensureCategoryIdsSynced() {
        List<Map<String, Object>> rows = questionMapper.selectMaps(
                new QueryWrapper<Question>()
                        .select("DISTINCT category")
                        .isNull("category_id")
                        .isNotNull("category")
                        .ne("category", "")
        );
        for (Map<String, Object> row : rows) {
            Object raw = row.get("category");
            if (raw == null) {
                continue;
            }
            String name = String.valueOf(raw).trim();
            if (name.isEmpty()) {
                continue;
            }
            Category category = categoryMapper.selectOne(Wrappers.<Category>lambdaQuery().eq(Category::getName, name).last("LIMIT 1"));
            if (category == null) {
                Category created = new Category();
                created.setName(name);
                categoryMapper.insert(created);
                category = created;
            }
            if (category.getId() == null) {
                continue;
            }
            questionMapper.update(
                    null,
                    Wrappers.<Question>lambdaUpdate()
                            .set(Question::getCategoryId, category.getId())
                            .isNull(Question::getCategoryId)
                            .eq(Question::getCategory, name)
            );
        }
    }
}
