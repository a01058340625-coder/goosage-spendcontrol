package com.goosage.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.api.view.spendcontrol.SpendControlEventRequest;
import com.goosage.app.spendcontrol.SpendControlEventService;
import com.goosage.auth.SessionConst;
import com.goosage.support.web.ApiResponse;

import jakarta.servlet.http.HttpSession;

@RestController
public class SpendControlEventController {

    private final SpendControlEventService spendControlEventService;

    public SpendControlEventController(SpendControlEventService spendControlEventService) {
        this.spendControlEventService = spendControlEventService;
    }

    @PostMapping("/spendcontrol/events")
    public ApiResponse<Void> record(@RequestBody SpendControlEventRequest req, HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (userId == null) {
            return ApiResponse.fail("로그인이 필요합니다");
        }

        spendControlEventService.record(
                userId,
                req.type(),
                req.knowledgeId(),
                null,
                false,
                null,
                null
        );

        return ApiResponse.ok(null);
    }
}