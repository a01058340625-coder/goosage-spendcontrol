package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.spendcontrol.SpendControlSnapshot;

@Component
public class ReviewWrongNeededRule implements PredictionRule {

    @Override
    public int priority() {
        return 10;
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

        return impulse > 0 && cancelDone == 0;
    }

    @Override
    public Prediction apply(SpendControlSnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.REVIEW_WRONG_PENDING,
                "충동 신호가 남아 있어. 하나씩 제어 행동으로 정리하자.",
                Map.of(
                        "studiedToday", s.studiedToday(),
                        "streakDays", s.streakDays(),
                        "daysSinceLastEvent", s.daysSinceLastEvent(),
                        "recentEventCount3d", s.recentEventCount3d(),
                        "eventsCount", s.state().eventsCount(),
                        "spendOpenCount", s.state().spendOpenCount(),
                        "itemViewCount", s.state().itemViewCount(),
                        "purchaseAttemptCount", s.state().purchaseAttemptCount(),
                        "impulseSignalCount", s.state().impulseSignalCount(),
                        "purchaseCancelDoneCount", s.state().purchaseCancelDoneCount()
                )
        );
    }
}