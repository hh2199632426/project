package com.racingquiz.backend.controller;

import com.racingquiz.backend.dto.CheckInResultRequest;
import com.racingquiz.backend.dto.CheckInStatusResponse;
import com.racingquiz.backend.entity.User;
import com.racingquiz.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/checkin")
@CrossOrigin(origins = "*")
public class CheckInController {

    private final UserService userService;

    public CheckInController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/status")
    public ResponseEntity<CheckInStatusResponse> getCheckInStatus(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        if (user != null) {
            CheckInStatusResponse status = userService.getCheckInStatus(user.getId());
            return ResponseEntity.ok(status);
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitCheckIn(Authentication authentication, @RequestBody CheckInResultRequest request) {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        if (user != null) {
            userService.processCheckIn(user.getId(), request.isSuccess());
            return ResponseEntity.ok(Map.of("message", "Check-in processed"));
        }
        return ResponseEntity.badRequest().build();
    }
}
