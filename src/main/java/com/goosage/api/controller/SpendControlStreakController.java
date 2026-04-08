package com.goosage.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.app.spendcontrol.SpendControlStreakService;
import com.goosage.auth.SessionConst;
import com.goosage.support.web.ApiResponse;

import jakarta.servlet.http.HttpSession;

@RestController
public class SpendControlStreakController {

    private final SpendControlStreakService recoveryStreakService;

    public SpendControlStreakController(SpendControlStreakService recoveryStreakService) {
        this.recoveryStreakService = recoveryStreakService;
    }

    @GetMapping("/recovery/streak")
    public ApiResponse<Integer> streak(HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (userId == null) {
            return ApiResponse.fail("로그인이 필요합니다");
        }

        int streak = recoveryStreakService.getStreak(userId);
        return ApiResponse.ok(streak);
    }
}