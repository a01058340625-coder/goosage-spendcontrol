package com.goosage.app.spendcontrol.interpret;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.goosage.domain.spendcontrol.SpendControlReadPort;
import com.goosage.domain.spendcontrol.SpendControlSnapshot;
import com.goosage.domain.spendcontrol.SpendControlState;

@Service
public class SpendControlInterpretationService {

    private final SpendControlReadPort spendControlReadPort;

    public SpendControlInterpretationService(SpendControlReadPort spendControlReadPort) {
        this.spendControlReadPort = spendControlReadPort;
    }

    public SpendControlState getEngineState(long userId) {
        return getSnapshot(userId).state();
    }

    public SpendControlSnapshot getSnapshot(long userId) {

        LocalDate today = LocalDate.now();

        var opt = spendControlReadPort.findToday(userId, today);
        LocalDateTime lastEventAtAll = spendControlReadPort.lastEventAtAll(userId).orElse(null);

        int streakDays = spendControlReadPort.calcStreakDays(userId, today);

        int events = spendControlReadPort.todayEventCountFromEvents(userId, today);
        int quiz = spendControlReadPort.todayActionFromEvents(userId, today);
        int wrong = spendControlReadPort.todayRiskSignalFromEvents(userId, today);
        int wrongDone = spendControlReadPort.todayRecoveryActionFromEvents(userId, today);

        System.out.println("[INTERPRET-SVC] user=" + userId);

        Long recentKnowledgeId = null;
        if (opt.isPresent()) {
            var row = opt.get();
            recentKnowledgeId = row.recentKnowledgeId();
        }

        SpendControlState state = new SpendControlState(wrong, quiz, events, wrongDone);

        int daysSinceLast = calcDaysSinceLastEvent(lastEventAtAll);
        int recent3d = spendControlReadPort.recentEventCount3d(userId, today);

        return new SpendControlSnapshot(
                today,
                state,
                events > 0,
                streakDays,
                lastEventAtAll,
                daysSinceLast,
                recent3d,
                recentKnowledgeId
        );
    }

    private int calcDaysSinceLastEvent(LocalDateTime lastEventAt) {
        if (lastEventAt == null) return 999;

        long days = java.time.Duration
                .between(lastEventAt, LocalDateTime.now())
                .toDays();

        return (int) Math.max(0, days);
    }
}