package com.goosage.api.internal;

import org.springframework.web.bind.annotation.*;

import com.goosage.app.spendcontrol.SpendControlCoachService;
import com.goosage.domain.spendcontrol.SpendControlCoachResult;

@RestController
public class InternalSpendControlCoachController {

    private final SpendControlCoachService spendControlCoachService;

    public InternalSpendControlCoachController(SpendControlCoachService spendControlCoachService) {
        this.spendControlCoachService = spendControlCoachService;
    }

    @GetMapping("/internal/spend/coach")
    public SpendControlCoachResult coach(
            @RequestHeader("X-INTERNAL-KEY") String internalKey,
            @RequestParam("userId") long userId
    ) {
        return spendControlCoachService.coach(userId);
    }
}