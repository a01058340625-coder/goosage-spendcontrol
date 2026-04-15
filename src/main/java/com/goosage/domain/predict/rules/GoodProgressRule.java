package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.spendcontrol.SpendControlSnapshot;

@Component
public class GoodProgressRule implements PredictionRule {

    private static final int EVENTS_MIN = 5;
    private static final int CANCEL_DONE_MIN = 2;
    private static final double CANCEL_RATIO_MIN = 0.30;
    private static final double PASSIVE_RATIO_MAX = 0.70;

    @Override
    public int priority() {
        return 60;
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

        if (impulse > 0) {
            return false;
        }

        if (cancelDone < attempt) {
            return false;
        }

        if (s.cancelDoneRatio() < CANCEL_RATIO_MIN) {
            return false;
        }

        double passiveRatio = s.openRatio() + s.viewRatio();
        if (passiveRatio > PASSIVE_RATIO_MAX) {
            return false;
        }

        return true;
    }

    @Override
    public Prediction apply(SpendControlSnapshot s) {
        return Prediction.of(
                PredictionLevel.SAFE,
                PredictionReasonCode.GOOD_PROGRESS,
                "소비 제어 흐름이 안정적으로 진행 중이다. 지금 패턴을 유지하자.",
                Map.of(
                        "eventsCount", s.state().eventsCount(),
                        "spendOpenCount", s.state().spendOpenCount(),
                        "itemViewCount", s.state().itemViewCount(),
                        "purchaseAttemptCount", s.state().purchaseAttemptCount(),
                        "purchaseCancelDoneCount", s.state().purchaseCancelDoneCount(),
                        "impulseSignalCount", s.state().impulseSignalCount(),
                        "cancelDoneRatio", s.cancelDoneRatio(),
                        "attemptRatio", s.attemptRatio(),
                        "openRatio", s.openRatio(),
                        "viewRatio", s.viewRatio()
                )
        );
    }
}