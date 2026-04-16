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
public class RelapseRiskRule implements PredictionRule {

    private static final int ATTEMPT_MIN = 4;
    private static final int IMPULSE_MIN = 1;
    private static final double PASSIVE_RATIO_MAX = 0.55;
    private static final double ATTEMPT_RATIO_MIN = 0.30;

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
        int impulse = s.state().impulseSignalCount();
        int cancelDone = s.state().purchaseCancelDoneCount();

        double passiveRatio = s.openRatio() + s.viewRatio();
        double attemptRatio = s.attemptRatio();

        boolean attemptHeavy = attempt >= ATTEMPT_MIN;
        boolean impulseExists = impulse >= IMPULSE_MIN;
        boolean controlWeak = cancelDone < attempt;
        boolean notJustBrowsing = passiveRatio <= PASSIVE_RATIO_MAX;
        boolean attemptDominant = attemptRatio >= ATTEMPT_RATIO_MIN;

        return attemptHeavy
                && impulseExists
                && controlWeak
                && notJustBrowsing
                && attemptDominant;
    }

    @Override
    public Prediction apply(SpendControlSnapshot s) {
        return Prediction.of(
                PredictionLevel.DANGER,
                PredictionReasonCode.RELAPSE_RISK,
                "실제 소비 재발 위험 구간이다. 소비 흐름을 끊고 제어 행동으로 바로 전환하자.",
                Map.ofEntries(
                        entry("eventsCount", s.state().eventsCount()),
                        entry("spendOpenCount", s.state().spendOpenCount()),
                        entry("itemViewCount", s.state().itemViewCount()),
                        entry("purchaseAttemptCount", s.state().purchaseAttemptCount()),
                        entry("impulseSignalCount", s.state().impulseSignalCount()),
                        entry("purchaseCancelDoneCount", s.state().purchaseCancelDoneCount()),
                        entry("attemptRatio", s.attemptRatio()),
                        entry("impulseRatio", s.impulseRatio()),
                        entry("cancelDoneRatio", s.cancelDoneRatio()),
                        entry("passiveRatio", s.openRatio() + s.viewRatio()),
                        entry("recentEventCount3d", s.recentEventCount3d()),
                        entry("streakDays", s.streakDays()),
                        entry("daysSinceLastEvent", s.daysSinceLastEvent())
                )
        );
    }
}