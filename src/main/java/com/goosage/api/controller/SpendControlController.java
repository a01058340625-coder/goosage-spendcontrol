package com.goosage.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.app.SpendControlTodayResult;
import com.goosage.app.SpendControlTodayService;
import com.goosage.auth.SessionConst;
import com.goosage.support.web.ApiResponse;

import jakarta.servlet.http.HttpSession;

@RestController
public class SpendControlController {

    private final SpendControlTodayService spendControlTodayService;

    public SpendControlController(SpendControlTodayService spendControlTodayService) {
        this.spendControlTodayService = spendControlTodayService;
    }

    @GetMapping("/spend/today")
    public ApiResponse<SpendControlTodayResult> today(HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (userId == null) {
            return ApiResponse.fail("로그인이 필요합니다");
        }

        SpendControlTodayResult result = spendControlTodayService.getToday(userId);
        return ApiResponse.ok(result);
    }
}