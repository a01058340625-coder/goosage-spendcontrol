package com.goosage.domain.spendcontrol;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * SpendControlSnapshot = SSOT
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

    public SpendControlSnapshot {
        if (state == null) {
            state = SpendControlState.empty();
        }
    }

    public double openRatio() {
        return ratio(state.spendOpenCount(), state.eventsCount());
    }

    public double viewRatio() {
        return ratio(state.itemViewCount(), state.eventsCount());
    }

    public double attemptRatio() {
        return ratio(state.purchaseAttemptCount(), state.eventsCount());
    }

    public double cancelDoneRatio() {
        return ratio(state.purchaseCancelDoneCount(), state.eventsCount());
    }

    public double impulseRatio() {
        return ratio(state.impulseSignalCount(), state.eventsCount());
    }

    public double controlActionRatio() {
        return ratio(state.controlActionCount(), state.eventsCount());
    }

    public double defensiveSuccessRatio() {
        int riskBase = state.purchaseAttemptCount() + state.impulseSignalCount();
        int defenseBase = state.purchaseCancelDoneCount() + state.controlActionCount();

        if (riskBase <= 0) {
            return defenseBase > 0 ? 1.0 : 0.0;
        }
        return (double) defenseBase / riskBase;
    }

    public boolean hasRiskSignal() {
        return state.purchaseAttemptCount() > 0 || state.impulseSignalCount() > 0;
    }

    public boolean hasControlProgress() {
        return state.purchaseCancelDoneCount() > 0 || state.controlActionCount() > 0;
    }

    public boolean isControlSafe() {
        int defense = state.purchaseCancelDoneCount() + state.controlActionCount();
        int risk = state.purchaseAttemptCount() + state.impulseSignalCount();

        return defense >= risk && risk > 0;
    }

    public int controlGap() {
        return state.controlGap();
    }

    public boolean isBlankState() {
        return state.isBlank();
    }

    private double ratio(int value, int total) {
        if (total <= 0) {
            return 0.0;
        }
        return (double) value / total;
    }
}