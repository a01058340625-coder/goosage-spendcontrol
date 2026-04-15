package com.goosage.api.view.spendcontrol;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SpendControlStateView {

    private LocalDate ymd;
    private boolean studiedToday;
    private int streakDays;
    private int eventsCount;
    private int spendOpenCount;
    private int itemViewCount;
    private int purchaseAttemptCount;
    private int purchaseCancelDoneCount;
    private int impulseSignalCount;
    private LocalDateTime lastEventAt;
    private Long recentKnowledgeId;

    public SpendControlStateView(
            LocalDate ymd,
            boolean studiedToday,
            int streakDays,
            int eventsCount,
            int spendOpenCount,
            int itemViewCount,
            int purchaseAttemptCount,
            int purchaseCancelDoneCount,
            int impulseSignalCount,
            LocalDateTime lastEventAt,
            Long recentKnowledgeId
    ) {
        this.ymd = ymd;
        this.studiedToday = studiedToday;
        this.streakDays = streakDays;
        this.eventsCount = eventsCount;
        this.spendOpenCount = spendOpenCount;
        this.itemViewCount = itemViewCount;
        this.purchaseAttemptCount = purchaseAttemptCount;
        this.purchaseCancelDoneCount = purchaseCancelDoneCount;
        this.impulseSignalCount = impulseSignalCount;
        this.lastEventAt = lastEventAt;
        this.recentKnowledgeId = recentKnowledgeId;
    }

    public LocalDate getYmd() {
        return ymd;
    }

    public boolean isStudiedToday() {
        return studiedToday;
    }

    public int getStreakDays() {
        return streakDays;
    }

    public int getEventsCount() {
        return eventsCount;
    }

    public int getSpendOpenCount() {
        return spendOpenCount;
    }

    public int getItemViewCount() {
        return itemViewCount;
    }

    public int getPurchaseAttemptCount() {
        return purchaseAttemptCount;
    }

    public int getPurchaseCancelDoneCount() {
        return purchaseCancelDoneCount;
    }

    public int getImpulseSignalCount() {
        return impulseSignalCount;
    }

    public LocalDateTime getLastEventAt() {
        return lastEventAt;
    }

    public Long getRecentKnowledgeId() {
        return recentKnowledgeId;
    }
}