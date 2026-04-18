package com.goosage.app.spendcontrol.interpret;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.goosage.domain.spendcontrol.SpendControlReadPort;
import com.goosage.domain.spendcontrol.SpendControlState;

@Service
public class SpendControlInterpretationService {

    private final SpendControlReadPort spendControlReadPort;

    public SpendControlInterpretationService(SpendControlReadPort spendControlReadPort) {
        this.spendControlReadPort = spendControlReadPort;
    }

    public SpendControlState interpret(long userId, LocalDate today) {
        int spendOpen = spendControlReadPort.todaySpendOpenCountFromEvents(userId, today);
        int itemView = spendControlReadPort.todayItemViewCountFromEvents(userId, today);
        int purchaseAttempt = spendControlReadPort.todayPurchaseAttemptCountFromEvents(userId, today);
        int purchaseCancelDone = spendControlReadPort.todayPurchaseCancelDoneCountFromEvents(userId, today);
        int impulseSignal = spendControlReadPort.todayImpulseSignalCountFromEvents(userId, today);
        int controlAction = spendControlReadPort.todayControlActionCountFromEvents(userId, today);

        int eventsCount = spendOpen
                + itemView
                + purchaseAttempt
                + purchaseCancelDone
                + impulseSignal
                + controlAction;

        return new SpendControlState(
                spendOpen,
                itemView,
                purchaseAttempt,
                purchaseCancelDone,
                impulseSignal,
                controlAction,
                eventsCount
        );
    }
}