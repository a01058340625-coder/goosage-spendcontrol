package com.goosage.domain.predict.rules;

import static java.util.Map.entry;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.spendcontrol.SpendControlSnapshot;

@Component
public class SpendControlConflictRule implements PredictionRule {

    private static final int EVENTS_MIN = 6;
    private static final int RISK_MIN = 3;
    private static final int DEFENSE_MIN = 2;
    private static final int GAP_MAX = 3;
    private static final double PASSIVE_RATIO_MAX = 0.75;

    @Override
    public int priority() {
        return 6;
    }

    @Override
    public boolean matches(SpendControlSnapshot s) {
        if (s == null || s.state() == null) {
            return false;
        }

        int events = s.state().eventsCount();
        int attempts = s.state().purchaseAttemptCount();
        int impulse = s.state().impulseSignalCount();
        int cancelDone = s.state().purchaseCancelDoneCount();
        int controlAction = s.state().controlActionCount();

        int risk = attempts + impulse;
        int defense = cancelDone + controlAction;
        int gap = Math.abs(risk - defense);

        if (events < EVENTS_MIN) {
            return false;
        }

        if (risk < RISK_MIN) {
            return false;
        }

        if (defense < DEFENSE_MIN) {
            return false;
        }

        if (gap > GAP_MAX) {
            return false;
        }

        double passiveRatio = s.openRatio() + s.viewRatio();

        if (passiveRatio > PASSIVE_RATIO_MAX) {
            return false;
        }

        return true;
    }

    @Override
    public Prediction apply(SpendControlSnapshot s) {
        int events = s.state() != null ? s.state().eventsCount() : 0;
        int open = s.state() != null ? s.state().spendOpenCount() : 0;
        int view = s.state() != null ? s.state().itemViewCount() : 0;
        int impulse = s.state() != null ? s.state().impulseSignalCount() : 0;
        int attempts = s.state() != null ? s.state().purchaseAttemptCount() : 0;
        int cancelDone = s.state() != null ? s.state().purchaseCancelDoneCount() : 0;
        int controlAction = s.state() != null ? s.state().controlActionCount() : 0;

        int risk = attempts + impulse;
        int defense = cancelDone + controlAction;
        int gap = Math.abs(risk - defense);
        double passiveRatio = s.openRatio() + s.viewRatio();

        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.RECOVERY_PROGRESS,
                "위험 신호와 제어 행동이 함께 나타난 혼합 구간이다. 성급히 안전으로 보지 말고 제어 흐름을 유지하자.",
                Map.ofEntries(
                        entry("streakDays", s.streakDays()),
                        entry("daysSinceLastEvent", s.daysSinceLastEvent()),
                        entry("recentEventCount3d", s.recentEventCount3d()),
                        entry("eventsCount", events),
                        entry("spendOpenCount", open),
                        entry("itemViewCount", view),
                        entry("impulseSignalCount", impulse),
                        entry("purchaseAttemptCount", attempts),
                        entry("purchaseCancelDoneCount", cancelDone),
                        entry("controlActionCount", controlAction),
                        entry("riskCount", risk),
                        entry("defenseCount", defense),
                        entry("gap", gap),
                        entry("passiveRatio", passiveRatio),
                        entry("passiveRatioMax", PASSIVE_RATIO_MAX)
                )
        );
    }
}