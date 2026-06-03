package com.racingquiz.backend.controller;

import com.racingquiz.backend.dto.ChangePasswordRequest;
import com.racingquiz.backend.dto.UpdateProfileRequest;
import com.racingquiz.backend.entity.User;
import com.racingquiz.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication authentication, @RequestBody UpdateProfileRequest request) {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        if (user != null) {
            userService.updateProfile(user.getId(), request.getNickName());
            return ResponseEntity.ok(Map.of("message", "资料修改成功"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "用户未找到"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(Authentication authentication, @RequestBody ChangePasswordRequest request) {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        if (user != null) {
            boolean success = userService.changePassword(user.getId(), request.getOldPassword(), request.getNewPassword());
            if (success) {
                return ResponseEntity.ok(Map.of("message", "密码修改成功"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "旧密码不正确"));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "用户未找到"));
    }
}
