package com.goosage.infra.adapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.goosage.domain.spendcontrol.SpendControlReadPort;
import com.goosage.domain.spendcontrol.TodayRow;
import com.goosage.infra.dao.SpendControlReadDao;

@Component
public class SpendControlReadDaoAdapter implements SpendControlReadPort {

    private final SpendControlReadDao dao;

    public SpendControlReadDaoAdapter(SpendControlReadDao dao) {
        this.dao = dao;
    }

    @Override
    public Optional<TodayRow> findToday(long userId, LocalDate today) {
        return dao.findToday(userId)
                .map(r -> new TodayRow(
                        r.ymd(),
                        r.eventsCount(),
                        r.spendOpenCount(),
                        r.itemViewCount(),
                        r.purchaseAttemptCount(),
                        r.purchaseCancelDoneCount(),
                        r.impulseSignalCount(),
                        null
                ));
    }

    @Override
    public Optional<LocalDateTime> lastEventAtAll(long userId) {
        return dao.lastEventAtAll(userId);
    }

    @Override
    public int recentEventCount3d(long userId, LocalDate today) {
        return dao.recentEventCount3d(userId, today);
    }

    @Override
    public int calcStreakDays(long userId, LocalDate today) {
        return dao.calcStreakDays(userId, today);
    }

    @Override
    public int todaySpendOpenCountFromEvents(long userId, LocalDate today) {
        return dao.todaySpendOpenCountFromEvents(userId, today);
    }

    @Override
    public int todayItemViewCountFromEvents(long userId, LocalDate today) {
        return dao.todayItemViewCountFromEvents(userId, today);
    }

    @Override
    public int todayPurchaseAttemptCountFromEvents(long userId, LocalDate today) {
        return dao.todayPurchaseAttemptCountFromEvents(userId, today);
    }

    @Override
    public int todayPurchaseCancelDoneCountFromEvents(long userId, LocalDate today) {
        return dao.todayPurchaseCancelDoneCountFromEvents(userId, today);
    }

    @Override
    public int todayImpulseSignalCountFromEvents(long userId, LocalDate today) {
        return dao.todayImpulseSignalCountFromEvents(userId, today);
    }

    @Override
    public int recentImpulseSignalCount3d(long userId, LocalDate today) {
        return dao.recentImpulseSignalCount3d(userId, today);
    }

    @Override
    public int recentPurchaseCancelDoneCount3d(long userId, LocalDate today) {
        return dao.recentPurchaseCancelDoneCount3d(userId, today);
    }
}