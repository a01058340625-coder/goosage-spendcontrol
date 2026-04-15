package com.goosage.app.spendcontrol;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.goosage.domain.spendcontrol.SpendControlReadPort;

@Service
public class SpendControlTodayService {

    private final SpendControlReadPort spendControlReadPort;

    public SpendControlTodayService(SpendControlReadPort spendControlReadPort) {
        this.spendControlReadPort = spendControlReadPort;
    }

    public Map<String, Object> today(long userId, LocalDate today) {
        int spendOpen = spendControlReadPort.todaySpendOpenCountFromEvents(userId, today);
        int itemView = spendControlReadPort.todayItemViewCountFromEvents(userId, today);
        int purchaseAttempt = spendControlReadPort.todayPurchaseAttemptCountFromEvents(userId, today);
        int purchaseCancelDone = spendControlReadPort.todayPurchaseCancelDoneCountFromEvents(userId, today);
        int impulseSignal = spendControlReadPort.todayImpulseSignalCountFromEvents(userId, today);

        int eventsCount = spendOpen + itemView + purchaseAttempt + purchaseCancelDone + impulseSignal;

        return Map.of(
                "userId", userId,
                "ymd", today,
                "eventsCount", eventsCount,
                "spendOpenCount", spendOpen,
                "itemViewCount", itemView,
                "purchaseAttemptCount", purchaseAttempt,
                "purchaseCancelDoneCount", purchaseCancelDone,
                "impulseSignalCount", impulseSignal
        );
    }
}