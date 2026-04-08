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

    private static final int RISK_MIN = 2;
    private static final int RECENT_3D_MIN = 4;

    @Override
    public int priority() {
        return 8;
    }

    @Override
    public boolean matches(SpendControlSnapshot s) {
        if (s == null || s.state() == null) return false;
        if (!s.studiedToday()) return false;

        int risk = s.state().wrongReviews();
        int done = s.state().wrongReviewDoneCount();

        return risk >= RISK_MIN
                && done == 0
                && s.recentEventCount3d() >= RECENT_3D_MIN;
    }

    @Override
    public Prediction apply(SpendControlSnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.URGE_HIGH,
                "소비 충동 강도가 높아지고 있다. 구매 전에 제어 행동 1회를 먼저 실행하자.",
                Map.of(
                        "riskSignal", s.state().wrongReviews(),
                        "recoveryAction", s.state().wrongReviewDoneCount(),
                        "recentEventCount3d", s.recentEventCount3d(),
                        "daysSinceLastEvent", s.daysSinceLastEvent()
                )
        );
    }
}