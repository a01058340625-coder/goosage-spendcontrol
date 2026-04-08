package com.goosage.domain.spendcontrol;

import java.time.LocalDate;

public record SpendControlTodayRow(
        LocalDate ymd,
        int eventsCount,
        int quizSubmits,
        int wrongReviews
) {}
