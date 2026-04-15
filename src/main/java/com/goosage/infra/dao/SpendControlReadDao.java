package com.goosage.infra.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface SpendControlReadDao {

    Optional<SpendControlTodayRow> findToday(long userId);

    Optional<LocalDateTime> lastEventAtAll(long userId);

    int recentEventCount3d(long userId, LocalDate today);

    int calcStreakDays(long userId, LocalDate today);

    int todaySpendOpenCountFromEvents(long userId, LocalDate today);

    int todayItemViewCountFromEvents(long userId, LocalDate today);

    int todayPurchaseAttemptCountFromEvents(long userId, LocalDate today);

    int todayPurchaseCancelDoneCountFromEvents(long userId, LocalDate today);

    int todayImpulseSignalCountFromEvents(long userId, LocalDate today);

    int recentImpulseSignalCount3d(long userId, LocalDate today);

    int recentPurchaseCancelDoneCount3d(long userId, LocalDate today);
}