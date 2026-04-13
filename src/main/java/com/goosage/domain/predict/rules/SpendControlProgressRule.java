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
        return s != null
                && s.state() != null
                && s.studiedToday()
                && s.state().wrongReviews() > 0
                && s.state().wrongReviewDoneCount() > 0;
    }

    @Override
    public Prediction apply(SpendControlSnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.RECOVERY_PROGRESS,
                "충동 신호를 제어하며 회복 중이야. 흐름을 이어가자.",
                Map.of(
                        "streakDays", s.streakDays(),
                        "daysSinceLastEvent", s.daysSinceLastEvent(),
                        "recentEventCount3d", s.recentEventCount3d(),
                        "eventsCount", s.state() != null ? s.state().eventsCount() : 0,
                        "wrongReviews", s.state() != null ? s.state().wrongReviews() : 0,
                        "wrongReviewDoneCount", s.state() != null ? s.state().wrongReviewDoneCount() : 0
                )
        );
    }
}