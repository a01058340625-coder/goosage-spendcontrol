package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.spendcontrol.SpendControlSnapshot;

@Component
public class AttemptChainRiskRule implements PredictionRule {

    private static final int ATTEMPT_MIN = 3;
    private static final double ATTEMPT_RATIO_MIN = 0.50;

    @Override
    public int priority() {
        return 9;
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
        int cancelDone = s.state().purchaseCancelDoneCount();
        int impulse = s.state().impulseSignalCount();

        return attempt >= ATTEMPT_MIN
                && s.attemptRatio() >= ATTEMPT_RATIO_MIN
                && cancelDone == 0
                && impulse == 0;
    }

    @Override
    public Prediction apply(SpendControlSnapshot s) {
        return Prediction.of(
                PredictionLevel.DANGER,
                PredictionReasonCode.RELAPSE_RISK,
                "소비 시도가 연속적으로 발생하고 있어. 즉시 제어 행동으로 전환하자.",
                Map.of(
                        "purchaseAttemptCount", s.state().purchaseAttemptCount(),
                        "purchaseCancelDoneCount", s.state().purchaseCancelDoneCount(),
                        "impulseSignalCount", s.state().impulseSignalCount(),
                        "attemptRatio", s.attemptRatio(),
                        "eventsCount", s.state().eventsCount(),
                        "recentEventCount3d", s.recentEventCount3d()
                )
        );
    }
}