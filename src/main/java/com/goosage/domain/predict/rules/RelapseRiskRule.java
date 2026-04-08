package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.spendcontrol.SpendControlSnapshot;

@Component
public class RelapseRiskRule implements PredictionRule {

    private static final int ACTION_MIN = 3;
    private static final int RISK_MIN = 1;
    private static final double OPEN_RATIO_MAX = 0.50;

    @Override
    public int priority() {
        return 9;
    }

    @Override
    public boolean matches(SpendControlSnapshot s) {
        if (s == null || s.state() == null) return false;
        if (!s.studiedToday()) return false;

        int actions = s.state().quizSubmits();
        int risk = s.state().wrongReviews();
        int done = s.state().wrongReviewDoneCount();

        boolean actionHeavy = actions >= ACTION_MIN;
        boolean riskExists = risk >= RISK_MIN;
        boolean controlWeak = done < risk;
        boolean notJustBrowsing = s.openRatio() <= OPEN_RATIO_MAX;

        return actionHeavy && riskExists && controlWeak && notJustBrowsing;
    }

    @Override
    public Prediction apply(SpendControlSnapshot s) {
        return Prediction.of(
                PredictionLevel.DANGER,
                PredictionReasonCode.RELAPSE_RISK,
                "실제 소비 재발 위험 구간이다. 소비 흐름을 끊고 제어 행동으로 바로 전환하자.",
                Map.of(
                        "actionCount", s.state().quizSubmits(),
                        "riskSignal", s.state().wrongReviews(),
                        "recoveryAction", s.state().wrongReviewDoneCount(),
                        "openRatio", s.openRatio(),
                        "quizRatio", s.quizRatio(),
                        "recentEventCount3d", s.recentEventCount3d()
                )
        );
    }
}