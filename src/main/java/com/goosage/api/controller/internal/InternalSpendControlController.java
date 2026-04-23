package com.goosage.api.controller.internal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.goosage.app.spendcontrol.SpendControlEventService;
import com.goosage.domain.EventType;
import com.goosage.domain.spendcontrol.SpendControlSnapshot;
import com.goosage.domain.spendcontrol.SpendControlSnapshotService;
import com.goosage.support.web.ApiResponse;

@RestController
@RequestMapping("/internal/spend")
public class InternalSpendControlController {

    private static final String INTERNAL_KEY = "goosage-dev";

    private final SpendControlEventService spendControlEventService;
    private final SpendControlSnapshotService spendControlSnapshotService;
    private final BrainTriggerService brainTriggerService;

    public InternalSpendControlController(
            SpendControlEventService spendControlEventService,
            SpendControlSnapshotService spendControlSnapshotService,
            BrainTriggerService brainTriggerService
    ) {
        this.spendControlEventService = spendControlEventService;
        this.spendControlSnapshotService = spendControlSnapshotService;
        this.brainTriggerService = brainTriggerService;
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> recordEvent(
            @RequestHeader(value = "X-INTERNAL-KEY", required = false) String key,
            @RequestHeader(value = "X-BRAIN-ACTION", required = false) String brainAction,
            @RequestBody Map<String, Object> body
    ) {

        System.out.println("[INTERNAL] hit /internal/spend/events");
        System.out.println("[INTERNAL] key=" + key);
        System.out.println("[INTERNAL] brainAction=" + brainAction);

        if (!INTERNAL_KEY.equals(key)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "bad internal key");
        }

        Long userId = toLong(body.get("userId"));
        String typeStr = (String) body.get("type");
        Long knowledgeId = toLongNullable(body.get("knowledgeId"));

        if (userId == null || typeStr == null || typeStr.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId/type required");
        }

        Object occurredAtRaw = body.get("occurredAt");
        LocalDateTime occurredAt = null;
        if (occurredAtRaw != null) {
            occurredAt = LocalDateTime.parse(occurredAtRaw.toString());
        }

        boolean isBrainAction = "true".equalsIgnoreCase(brainAction);
        String brainActionType = toStringNullable(body.get("brainActionType"));
        String brainPatternType = toStringNullable(body.get("brainPatternType"));

        spendControlEventService.record(
                userId,
                EventType.valueOf(typeStr),
                knowledgeId,
                occurredAt,
                isBrainAction,
                brainActionType,
                brainPatternType
        );

        if (!isBrainAction) {
            try {
                SpendControlSnapshot snapshot = spendControlSnapshotService.snapshot(
                        userId,
                        LocalDate.now(),
                        LocalDateTime.now()
                );

                brainTriggerService.triggerSpendSnapshot(
                        userId,
                        snapshot.recentEventCount3d(),
                        snapshot.streakDays(),
                        snapshot.daysSinceLastEvent(),
                        snapshot.state().spendOpenCount(),
                        snapshot.state().itemViewCount(),
                        snapshot.state().purchaseAttemptCount(),
                        snapshot.state().purchaseCancelDoneCount(),
                        snapshot.state().impulseSignalCount()
                );

                System.out.println("[BRAIN_TRIGGER][SPEND] success userId=" + userId);

            } catch (Exception e) {
                System.out.println("[BRAIN_TRIGGER][SPEND] fail userId=" + userId + " error=" + e.getMessage());
            }
        } else {
            System.out.println("[BRAIN_TRIGGER][SPEND] skipped brain action userId=" + userId);
        }

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
    
    private String toStringNullable(Object v) {
        if (v == null) return null;
        String s = v.toString();
        return s.isBlank() ? null : s;
    }
}