package com.racingquiz.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.racingquiz.backend.dto.LoginRequest;
import com.racingquiz.backend.entity.User;
import com.racingquiz.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.racingquiz.backend.dto.CheckInStatusResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User login(LoginRequest request) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", request.getUsername());
        User user = userMapper.selectOne(queryWrapper);

        if (user != null && user.getPassword().equals(request.getPassword())) {
            return user;
        }
        return null;
    }

    public User getUserById(Integer userId) {
        return userMapper.selectById(userId);
    }

    public User getUserByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }

    public void updateProfile(Integer userId, String nickName) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setNickName(nickName);
            userMapper.updateById(user);
        }
    }

    public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user != null && user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword);
            userMapper.updateById(user);
            return true;
        }
        return false;
    }

    public CheckInStatusResponse getCheckInStatus(Integer userId) {
        User user = userMapper.selectById(userId);
        CheckInStatusResponse response = new CheckInStatusResponse();
        if (user != null) {
            LocalDate today = LocalDate.now();
            boolean hasCheckedIn = today.equals(user.getLastCheckinDate());
            response.setHasCheckedIn(hasCheckedIn);
            response.setTotalCheckinDays(user.getTotalCheckinDays() == null ? 0 : user.getTotalCheckinDays());
        }
        return response;
    }

    public void processCheckIn(Integer userId, boolean success) {
        if (!success) return;
        
        User user = userMapper.selectById(userId);
        if (user != null) {
            LocalDate today = LocalDate.now();
            if (!today.equals(user.getLastCheckinDate())) {
                int currentDays = user.getTotalCheckinDays() == null ? 0 : user.getTotalCheckinDays();
                user.setTotalCheckinDays(currentDays + 1);
                user.setLastCheckinDate(today);
                userMapper.updateById(user);
            }
        }
    }
}
