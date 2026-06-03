package com.racingquiz.backend.controller;

import com.racingquiz.backend.dto.AdminOverviewDto;
import com.racingquiz.backend.dto.AdminQuestionDto;
import com.racingquiz.backend.dto.AdminUserDto;
import com.racingquiz.backend.dto.PageResponse;
import com.racingquiz.backend.dto.AdminCategoryDto;
import com.racingquiz.backend.dto.AdminCreateCategoryRequest;
import com.racingquiz.backend.dto.AdminCreateQuestionRequest;
import com.racingquiz.backend.dto.AdminCreateUserRequest;
import com.racingquiz.backend.dto.LoginRequest;
import com.racingquiz.backend.security.JwtTokenService;
import com.racingquiz.backend.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    private final AdminService adminService;
    private final JwtTokenService jwtTokenService;

    public AdminController(AdminService adminService, JwtTokenService jwtTokenService) {
        this.adminService = adminService;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        boolean success = adminService.login(request.getUsername(), request.getPassword());
        if (!success) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid admin username or password");
        }
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Admin login successful");
        response.put("admin", Map.of("username", request.getUsername()));
        response.put("token", jwtTokenService.createToken(request.getUsername(), "ADMIN"));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overview")
    public AdminOverviewDto getOverview() {
        return adminService.getOverview();
    }

    @GetMapping("/users")
    public PageResponse<AdminUserDto> getUsers(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String username
    ) {
        return adminService.getUsers(page, size, username);
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody AdminCreateUserRequest request) {
        try {
            return ResponseEntity.ok(adminService.createUser(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody AdminCreateUserRequest request) {
        try {
            adminService.updateUser(userId, request);
            return ResponseEntity.ok(Map.of("message", "更新成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }

    @GetMapping("/questions")
    public PageResponse<AdminQuestionDto> getQuestions(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String questionText
    ) {
        return adminService.getQuestions(page, size, categoryId, questionText);
    }

    @PostMapping("/questions")
    public ResponseEntity<?> createQuestion(@RequestBody AdminCreateQuestionRequest request) {
        try {
            return ResponseEntity.ok(adminService.createQuestion(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/questions/{questionId}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long questionId, @RequestBody AdminCreateQuestionRequest request) {
        try {
            adminService.updateQuestion(questionId, request);
            return ResponseEntity.ok(Map.of("message", "更新成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long questionId) {
        adminService.deleteQuestion(questionId);
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }

    @GetMapping("/categories")
    public List<AdminCategoryDto> getCategories() {
        return adminService.getCategories();
    }

    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(@RequestBody AdminCreateCategoryRequest request) {
        try {
            return ResponseEntity.ok(adminService.createCategory(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/categories/{categoryId}")
    public Map<String, Object> deleteCategory(@PathVariable Long categoryId) {
        return adminService.deleteCategory(categoryId);
    }
}
