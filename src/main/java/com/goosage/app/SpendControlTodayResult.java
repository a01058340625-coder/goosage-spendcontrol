package com.goosage.app;

public record SpendControlTodayResult(
        int eventsCount,
        int quizSubmits,
        int wrongReviews,
        String message
) {}
