package com.goosage.domain.spendcontrol;

public interface DailyLearningPort {
    void upsertToday(long userId, boolean isQuizSubmit, boolean isReviewWrong);
}