package com.goosage.api.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.app.spendcontrol.SpendControlTodayService;

import jakarta.servlet.http.HttpSession;

@RestController
public class SpendControlController {

    private final SpendControlTodayService spendControlTodayService;

    public SpendControlController(SpendControlTodayService spendControlTodayService) {
        this.spendControlTodayService = spendControlTodayService;
    }

    @GetMapping("/spend/today")
    public Map<String, Object> today(HttpSession session) {
        Object loginUserId = session.getAttribute("LOGIN_USER_ID");
        if (loginUserId == null) {
            return Map.of("ok", false, "message", "login required");
        }

        long userId = ((Number) loginUserId).longValue();
        return spendControlTodayService.today(userId, LocalDate.now());
    }
}