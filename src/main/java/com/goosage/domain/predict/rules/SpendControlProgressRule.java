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
public class SpendControlProgressRule implements PredictionRule {

    @Override
    public int priority() {
        return 8;
    }

    @Override
    public boolean matches(SpendControlSnapshot s) {
        if (s == null || s.state() == null || !s.studiedToday()) {
            return false;
        }

        int impulse = s.state().impulseSignalCount();
        int attempts = s.state().purchaseAttemptCount();
        int cancelDone = s.state().purchaseCancelDoneCount();
        int controlAction = s.state().controlActionCount();
        int events = s.state().eventsCount();

        int risk = attempts + impulse;
        int defense = cancelDone + controlAction;

        if (events < 4) {
            return false;
        }

        if (defense <= 0) {
            return false;
        }

        if (risk <= 0) {
            return false;
        }

        if (defense < risk) {
            return false;
        }

        if (defense >= risk + 2 && events >= 6) {
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

        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.RECOVERY_PROGRESS,
                "위험 흐름 속에서도 제어 행동이 붙고 있다. 제어 흐름을 유지하자.",
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
                        entry("defenseCount", defense)
                )
        );
    }
}