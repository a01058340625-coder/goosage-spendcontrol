package com.goosage.domain.predict.rules;

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
        return 12;
    }

    @Override
    public boolean matches(SpendControlSnapshot s) {
        if (s == null || s.state() == null || !s.studiedToday()) {
            return false;
        }

        int impulse = s.state().impulseSignalCount();
        int attempts = s.state().purchaseAttemptCount();
        int cancelDone = s.state().purchaseCancelDoneCount();
        int events = s.state().eventsCount();

        if (cancelDone <= 0) {
            return false;
        }

        if (impulse == 0 && attempts <= 1 && cancelDone >= 4 && events >= 5) {
            return false;
        }

        if (attempts <= 1 && cancelDone <= 1) {
            return false;
        }

        return attempts >= 2 || impulse >= 1;
    }

    @Override
    public Prediction apply(SpendControlSnapshot s) {
        int events = s.state() != null ? s.state().eventsCount() : 0;
        int open = s.state() != null ? s.state().spendOpenCount() : 0;
        int view = s.state() != null ? s.state().itemViewCount() : 0;
        int impulse = s.state() != null ? s.state().impulseSignalCount() : 0;
        int attempts = s.state() != null ? s.state().purchaseAttemptCount() : 0;
        int cancelDone = s.state() != null ? s.state().purchaseCancelDoneCount() : 0;

        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.RECOVERY_PROGRESS,
                "소비 시도 흐름 속에서도 제어 행동이 붙고 있어. 제어 흐름을 이어가자.",
                Map.of(
                        "streakDays", s.streakDays(),
                        "daysSinceLastEvent", s.daysSinceLastEvent(),
                        "recentEventCount3d", s.recentEventCount3d(),
                        "eventsCount", events,
                        "spendOpenCount", open,
                        "itemViewCount", view,
                        "impulseSignalCount", impulse,
                        "purchaseAttemptCount", attempts,
                        "purchaseCancelDoneCount", cancelDone
                )
        );
    }
}