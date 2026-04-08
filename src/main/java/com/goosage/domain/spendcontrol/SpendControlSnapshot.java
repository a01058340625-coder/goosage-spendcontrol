package com.goosage.domain.spendcontrol;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * SpendControlSnapshot = SSOT(단일 진실)
 * - Coach / Prediction / NextAction이 참고하는 읽기 전용 스냅샷
 * - Rule / Engine / Controller는 DB를 직접 보지 않고 Snapshot만 본다.
 */
public record SpendControlSnapshot(
        LocalDate ymd,
        SpendControlState state,
        boolean studiedToday,
        int streakDays,
        LocalDateTime lastEventAt,
        int daysSinceLastEvent,
        int recentEventCount3d,
        Long recentKnowledgeId
) {

    public double openRatio() {
        if (state == null || state.eventsCount() <= 0) return 0.0;
        return (double) state.justOpenCount() / state.eventsCount();
    }

    public double quizRatio() {
        if (state == null || state.eventsCount() <= 0) return 0.0;
        return (double) state.quizSubmits() / state.eventsCount();
    }

    public double wrongRatio() {
        if (state == null || state.eventsCount() <= 0) return 0.0;
        return (double) state.wrongReviews() / state.eventsCount();
    }

    public double wrongDoneRatio() {
        if (state == null || state.eventsCount() <= 0) return 0.0;
        return (double) state.wrongReviewDoneCount() / state.eventsCount();
    }

    public boolean hasRiskSignal() {
        return state != null && state.wrongReviews() > 0;
    }

    public boolean hasControlProgress() {
        return state != null && state.wrongReviewDoneCount() > 0;
    }

    public boolean isControlSafe() {
        return state != null && state.wrongReviewDoneCount() > state.wrongReviews();
    }
}