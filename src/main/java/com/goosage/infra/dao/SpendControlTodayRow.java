package com.goosage.infra.dao;

import java.time.LocalDate;

public record SpendControlTodayRow(
        LocalDate ymd,
        int eventsCount,
        int spendOpenCount,
        int itemViewCount,
        int purchaseAttemptCount,
        int purchaseCancelDoneCount,
        int impulseSignalCount
) {
}