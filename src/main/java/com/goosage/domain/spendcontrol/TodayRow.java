package com.goosage.domain.spendcontrol;

import java.time.LocalDate;

public record TodayRow(
        LocalDate ymd,
        int eventsCount,
        int spendOpenCount,
        int itemViewCount,
        int purchaseAttemptCount,
        int purchaseCancelDoneCount,
        int impulseSignalCount,
        int controlActionCount,
        Long recentKnowledgeId
) {
}