package com.goosage.domain.spendcontrol;

public record SpendControlState(
        int spendOpenCount,
        int itemViewCount,
        int purchaseAttemptCount,
        int purchaseCancelDoneCount,
        int impulseSignalCount,
        int eventsCount
) {

    public SpendControlState {
        spendOpenCount = Math.max(spendOpenCount, 0);
        itemViewCount = Math.max(itemViewCount, 0);
        purchaseAttemptCount = Math.max(purchaseAttemptCount, 0);
        purchaseCancelDoneCount = Math.max(purchaseCancelDoneCount, 0);
        impulseSignalCount = Math.max(impulseSignalCount, 0);
        eventsCount = Math.max(eventsCount, 0);
    }

    public static SpendControlState empty() {
        return new SpendControlState(0, 0, 0, 0, 0, 0);
    }

    public int controlGap() {
        return Math.max((purchaseAttemptCount + impulseSignalCount) - purchaseCancelDoneCount, 0);
    }

    public boolean hasAttempt() {
        return purchaseAttemptCount > 0;
    }

    public boolean hasImpulse() {
        return impulseSignalCount > 0;
    }

    public boolean hasCancelDone() {
        return purchaseCancelDoneCount > 0;
    }

    public boolean hasViewBias() {
        return itemViewCount > 0 && purchaseAttemptCount <= 0 && impulseSignalCount <= 0;
    }

    public boolean isBlank() {
        return eventsCount <= 0;
    }
}