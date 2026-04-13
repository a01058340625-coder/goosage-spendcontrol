package com.goosage.api.controller.internal;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;

import com.goosage.app.spendcontrol.SpendControlEventService;
import com.goosage.domain.EventType;
import com.goosage.support.web.ApiResponse;

@RestController
@RequestMapping("/internal/spend")
public class InternalSpendControlController {

    private static final String INTERNAL_KEY = "goosage-dev";

    private final SpendControlEventService spendControlEventService;

    public InternalSpendControlController(SpendControlEventService spendControlEventService) {
        this.spendControlEventService = spendControlEventService;
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> recordEvent(
            @RequestHeader(value = "X-INTERNAL-KEY", required = false) String key,
            @RequestBody Map<String, Object> body
    ) {

        System.out.println("[INTERNAL] hit /internal/spend/events");
        System.out.println("[INTERNAL] key=" + key);

        if (!INTERNAL_KEY.equals(key)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "bad internal key");
        }

        Long userId = toLong(body.get("userId"));
        String typeStr = (String) body.get("type");
        Long knowledgeId = toLongNullable(body.get("knowledgeId"));

        if (userId == null || typeStr == null || typeStr.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId/type required");
        }

        spendControlEventService.record(userId, EventType.valueOf(typeStr), knowledgeId);

        return ApiResponse.ok(null);
    }

    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        return Long.valueOf(v.toString());
    }

    private Long toLongNullable(Object v) {
        if (v == null) return null;
        String s = v.toString();
        if (s.isBlank() || "null".equalsIgnoreCase(s)) return null;
        return Long.valueOf(s);
    }
}