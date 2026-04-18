package com.goosage.domain.spendcontrol;

public record SpendControlState(
        int spendOpenCount,
        int itemViewCount,
        int purchaseAttemptCount,
        int purchaseCancelDoneCount,
        int impulseSignalCount,
        int controlActionCount,
        int eventsCount
) {

    public static SpendControlState empty() {
        return new SpendControlState(0, 0, 0, 0, 0, 0, 0);
    }

    public int riskCount() {
        return purchaseAttemptCount + impulseSignalCount;
    }

    public int defenseCount() {
        return purchaseCancelDoneCount + controlActionCount;
    }

    public int controlGap() {
        return riskCount() - defenseCount();
    }

    public boolean isBlank() {
        return eventsCount <= 0;
    }
}