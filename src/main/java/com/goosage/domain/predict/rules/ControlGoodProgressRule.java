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
public class ControlGoodProgressRule implements PredictionRule {

    private static final int CANCEL_DONE_MIN = 2;
    private static final int RECENT_3D_MIN = 3;

    @Override
    public int priority() {
        return 14;
    }

    @Override
    public boolean matches(SpendControlSnapshot s) {
        if (s == null || s.state() == null) {
            return false;
        }

        if (!s.studiedToday()) {
            return false;
        }

        int attempt = s.state().purchaseAttemptCount();
        int impulse = s.state().impulseSignalCount();
        int cancelDone = s.state().purchaseCancelDoneCount();

        if (cancelDone < CANCEL_DONE_MIN) {
            return false;
        }

        if (cancelDone < (attempt + impulse)) {
            return false;
        }

        if (s.recentEventCount3d() < RECENT_3D_MIN) {
            return false;
        }

        return true;
    }

    @Override
    public Prediction apply(SpendControlSnapshot s) {
        return Prediction.of(
                PredictionLevel.SAFE,
                PredictionReasonCode.GOOD_PROGRESS,
                "충동과 소비 시도를 제어 완료 흐름이 안정적으로 덮고 있다.",
                Map.ofEntries(
                        entry("spendOpenCount", s.state().spendOpenCount()),
                        entry("itemViewCount", s.state().itemViewCount()),
                        entry("purchaseAttemptCount", s.state().purchaseAttemptCount()),
                        entry("impulseSignalCount", s.state().impulseSignalCount()),
                        entry("purchaseCancelDoneCount", s.state().purchaseCancelDoneCount()),
                        entry("eventsCount", s.state().eventsCount()),
                        entry("cancelDoneRatio", s.cancelDoneRatio()),
                        entry("attemptRatio", s.attemptRatio()),
                        entry("impulseRatio", s.impulseRatio()),
                        entry("recentEventCount3d", s.recentEventCount3d()),
                        entry("streakDays", s.streakDays()),
                        entry("daysSinceLastEvent", s.daysSinceLastEvent())
                )
        );
    }
}