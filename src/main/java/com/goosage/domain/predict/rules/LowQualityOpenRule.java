package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.spendcontrol.SpendControlSnapshot;

@Component
public class LowQualityOpenRule implements PredictionRule {

    private static final int EVENTS_MIN = 5;
    private static final int IMPULSE_BLOCK_MIN = 3;
    private static final double PASSIVE_RATIO_MIN = 0.55;
    private static final double ATTEMPT_RATIO_MAX = 0.45;

    @Override
    public int priority() {
        return 20;
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
        int attempts = s.state().purchaseAttemptCount();
        int cancelDone = s.state().purchaseCancelDoneCount();
        int impulse = s.state().impulseSignalCount();

        double passiveRatio = s.openRatio() + s.viewRatio();
        double attemptRatio = s.attemptRatio();

        System.out.println(
                "[LOW_QUALITY_OPEN] events=" + events
                + ", attempts=" + attempts
                + ", cancelDone=" + cancelDone
                + ", impulse=" + impulse
                + ", passiveRatio=" + passiveRatio
                + ", attemptRatio=" + attemptRatio
        );

        if (impulse >= IMPULSE_BLOCK_MIN) {
            return false;
        }

        if (cancelDone >= 3) {
            return false;
        }

        if (attempts > 0 && cancelDone >= attempts) {
            return false;
        }

        if (attempts == 0 && impulse == 0) {
            return false;
        }

        return events >= EVENTS_MIN
                && passiveRatio >= PASSIVE_RATIO_MIN
                && attemptRatio <= ATTEMPT_RATIO_MAX;
    }

    @Override
    public Prediction apply(SpendControlSnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.LOW_QUALITY_OPEN,
                "열기/탐색 비중이 높고 제어가 부족해. 실제 소비 제어 행동을 1개 추가하자.",
                Map.of(
                        "passiveRatio", s.openRatio() + s.viewRatio(),
                        "attemptRatio", s.attemptRatio(),
                        "impulseSignalCount", s.state().impulseSignalCount(),
                        "eventsCount", s.state().eventsCount(),
                        "studiedToday", s.studiedToday()
                )
        );
    }
}