package com.goosage.domain.spendcontrol;

public record SpendControlState(
        int wrongReviews,
        int quizSubmits,
        int eventsCount,
        int wrongReviewDoneCount
) {

    public int justOpenCount() {
        int value = eventsCount - quizSubmits - wrongReviews - wrongReviewDoneCount;
        return Math.max(value, 0);
    }
}