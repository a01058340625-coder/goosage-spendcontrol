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
public class StableProgressRule implements PredictionRule {

    private static final int EVENTS_MIN = 4;
    private static final int CANCEL_DONE_MIN = 1;
    private static final double IMPULSE_RATIO_MAX = 0.25;
    private static final double CANCEL_DONE_RATIO_MIN = 0.15;

    @Override
    public int priority() {
        return 50;
    }

    @Override
    public boolean matches(SpendControlSnapshot s) {
        if (s == null || s.state() == null) {
            return false;
        }

        if (!s.studiedToday()) {
            return false;
        }

        int events = s.state().eventsCount();
        int attempt = s.state().purchaseAttemptCount();
        int impulse = s.state().impulseSignalCount();
        int cancelDone = s.state().purchaseCancelDoneCount();

        if (events < EVENTS_MIN) {
            return false;
        }

        if (cancelDone < CANCEL_DONE_MIN) {
            return false;
        }

        if (attempt <= 0 && impulse <= 0) {
            return false;
        }

        if (s.impulseRatio() > IMPULSE_RATIO_MAX) {
            return false;
        }

        if (s.cancelDoneRatio() < CANCEL_DONE_RATIO_MIN) {
            return false;
        }

        return cancelDone < (attempt + impulse);
    }

    @Override
    public Prediction apply(SpendControlSnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.STABLE_PROGRESS,
                "완전 안전 단계는 아니지만 제어 흐름이 안정적으로 붙고 있다.",
                Map.ofEntries(
                        entry("eventsCount", s.state().eventsCount()),
                        entry("spendOpenCount", s.state().spendOpenCount()),
                        entry("itemViewCount", s.state().itemViewCount()),
                        entry("purchaseAttemptCount", s.state().purchaseAttemptCount()),
                        entry("purchaseCancelDoneCount", s.state().purchaseCancelDoneCount()),
                        entry("impulseSignalCount", s.state().impulseSignalCount()),
                        entry("attemptRatio", s.attemptRatio()),
                        entry("cancelDoneRatio", s.cancelDoneRatio()),
                        entry("impulseRatio", s.impulseRatio()),
                        entry("recentEventCount3d", s.recentEventCount3d()),
                        entry("streakDays", s.streakDays()),
                        entry("daysSinceLastEvent", s.daysSinceLastEvent())
                )
        );
    }
}