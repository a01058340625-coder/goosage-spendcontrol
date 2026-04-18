package com.goosage.domain.spendcontrol;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface SpendControlReadPort {

    Optional<TodayRow> findToday(long userId, LocalDate today);

    Optional<LocalDateTime> lastEventAtAll(long userId);

    int recentEventCount3d(long userId, LocalDate today);

    int calcStreakDays(long userId, LocalDate today);

    int todaySpendOpenCountFromEvents(long userId, LocalDate today);

    int todayItemViewCountFromEvents(long userId, LocalDate today);

    int todayPurchaseAttemptCountFromEvents(long userId, LocalDate today);

    int todayPurchaseCancelDoneCountFromEvents(long userId, LocalDate today);

    int todayImpulseSignalCountFromEvents(long userId, LocalDate today);

    int todayControlActionCountFromEvents(long userId, LocalDate today);

    int recentImpulseSignalCount3d(long userId, LocalDate today);

    int recentPurchaseCancelDoneCount3d(long userId, LocalDate today);

    int recentControlActionCount3d(long userId, LocalDate today);
}