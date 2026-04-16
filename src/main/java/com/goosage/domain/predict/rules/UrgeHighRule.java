package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.spendcontrol.SpendControlSnapshot;

@Component
public class UrgeHighRule implements PredictionRule {

    private static final int IMPULSE_MIN = 3;
    private static final int RECENT_3D_MIN = 4;
    private static final int ATTEMPT_MAX = 2;
    private static final int CANCEL_MAX = 1;
    private static final double IMPULSE_RATIO_MIN = 0.25;

    @Override
    public int priority() {
        return 8;
    }

    @Override
    public boolean matches(SpendControlSnapshot s) {
        if (s == null || s.state() == null) {
            return false;
        }

        if (!s.studiedToday()) {
            return false;
        }

        int impulse = s.state().impulseSignalCount();
        int cancelDone = s.state().purchaseCancelDoneCount();
        int attempt = s.state().purchaseAttemptCount();

        boolean impulseHigh = impulse >= IMPULSE_MIN;
        boolean recentEnough = s.recentEventCount3d() >= RECENT_3D_MIN;
        boolean attemptNotDominant = attempt <= ATTEMPT_MAX;
        boolean cancelStillWeak = cancelDone <= CANCEL_MAX;
        boolean impulseDominant = s.impulseRatio() >= IMPULSE_RATIO_MIN;

        return impulseHigh
                && recentEnough
                && attemptNotDominant
                && cancelStillWeak
                && impulseDominant;
    }

    @Override
    public Prediction apply(SpendControlSnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.URGE_HIGH,
                "소비 충동 강도가 높아지고 있다. 구매 전에 제어 행동 1회를 먼저 실행하자.",
                Map.of(
                        "impulseSignalCount", s.state().impulseSignalCount(),
                        "purchaseCancelDoneCount", s.state().purchaseCancelDoneCount(),
                        "purchaseAttemptCount", s.state().purchaseAttemptCount(),
                        "recentEventCount3d", s.recentEventCount3d(),
                        "impulseRatio", s.impulseRatio(),
                        "daysSinceLastEvent", s.daysSinceLastEvent()
                )
        );
    }
}