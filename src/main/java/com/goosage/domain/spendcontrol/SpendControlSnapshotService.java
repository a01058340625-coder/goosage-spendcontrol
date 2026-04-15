package com.goosage.domain.spendcontrol;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

@Service
public class SpendControlSnapshotService {

    private final SpendControlReadPort readPort;

    public SpendControlSnapshotService(SpendControlReadPort readPort) {
        this.readPort = readPort;
    }

    public SpendControlSnapshot snapshot(long userId, LocalDate nowDate, LocalDateTime nowDateTime) {

        var opt = readPort.findToday(userId, nowDate);

        LocalDateTime lastEventAtAll = readPort.lastEventAtAll(userId).orElse(null);
        int streakDays = readPort.calcStreakDays(userId, nowDate);

        int spendOpenCount = readPort.todaySpendOpenCountFromEvents(userId, nowDate);
        int itemViewCount = readPort.todayItemViewCountFromEvents(userId, nowDate);
        int purchaseAttemptCount = readPort.todayPurchaseAttemptCountFromEvents(userId, nowDate);
        int purchaseCancelDoneCount = readPort.todayPurchaseCancelDoneCountFromEvents(userId, nowDate);
        int impulseSignalCount = readPort.todayImpulseSignalCountFromEvents(userId, nowDate);

        int eventsCount = spendOpenCount
                + itemViewCount
                + purchaseAttemptCount
                + purchaseCancelDoneCount
                + impulseSignalCount;

        System.out.println("[SNAPSHOT-SVC] user=" + userId
                + " events=" + eventsCount
                + " open=" + spendOpenCount
                + " view=" + itemViewCount
                + " attempt=" + purchaseAttemptCount
                + " cancelDone=" + purchaseCancelDoneCount
                + " impulse=" + impulseSignalCount);

        Long recentKnowledgeId = null;
        boolean studiedToday = eventsCount > 0;

        if (opt.isPresent()) {
            var a = opt.get();
            recentKnowledgeId = a.recentKnowledgeId();
        }

        SpendControlState state = new SpendControlState(
                spendOpenCount,
                itemViewCount,
                purchaseAttemptCount,
                purchaseCancelDoneCount,
                impulseSignalCount,
                eventsCount
        );

        int daysSinceLast = calcDaysSinceLastEvent(lastEventAtAll, nowDateTime);
        int recent3d = readPort.recentEventCount3d(userId, nowDate);

        return new SpendControlSnapshot(
                nowDate,
                state,
                studiedToday,
                streakDays,
                lastEventAtAll,
                daysSinceLast,
                recent3d,
                recentKnowledgeId
        );
    }

    private int calcDaysSinceLastEvent(LocalDateTime lastEventAt, LocalDateTime now) {
        if (lastEventAt == null) {
            return 999;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(
                lastEventAt.toLocalDate(),
                now.toLocalDate()
        );
    }
}