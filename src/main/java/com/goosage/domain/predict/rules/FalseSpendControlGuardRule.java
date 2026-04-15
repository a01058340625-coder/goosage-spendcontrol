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
public class FalseSpendControlGuardRule implements PredictionRule {

    private static final int RECENT_3D_MIN = 3;
    private static final double PASSIVE_RATIO_MAX_FOR_SAFE = 0.75;
    private static final double ATTEMPT_RATIO_MIN_FOR_SAFE = 0.20;

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

        int events = s.state().eventsCount();
        int attempt = s.state().purchaseAttemptCount();
        int impulse = s.state().impulseSignalCount();
        int cancelDone = s.state().purchaseCancelDoneCount();

        if (events <= 0) {
            return false;
        }

        double passiveRatio = s.openRatio() + s.viewRatio();
        double attemptRatio = s.attemptRatio();

        boolean recentEnough = s.recentEventCount3d() >= RECENT_3D_MIN;
        boolean stillRiskLeft = impulse > 0;
        boolean tooManyPassive = passiveRatio > PASSIVE_RATIO_MAX_FOR_SAFE;
        boolean tooLittleAttempt = attemptRatio < ATTEMPT_RATIO_MIN_FOR_SAFE;
        boolean noControlDone = cancelDone == 0;

        if (impulse == 0 && attempt == 0 && cancelDone == 0) {
            return false;
        }

        if (impulse == 0 && cancelDone >= 3) {
            return false;
        }

        if (attempt <= 1 && cancelDone >= 1 && impulse <= 1) {
            return false;
        }

        return recentEnough
                && (stillRiskLeft || tooManyPassive || (tooLittleAttempt && noControlDone));
    }

    @Override
    public Prediction apply(SpendControlSnapshot s) {
        int events = s.state().eventsCount();
        int open = s.state().spendOpenCount();
        int view = s.state().itemViewCount();
        int attempt = s.state().purchaseAttemptCount();
        int impulse = s.state().impulseSignalCount();
        int cancelDone = s.state().purchaseCancelDoneCount();

        double passiveRatio = s.openRatio() + s.viewRatio();
        double attemptRatio = s.attemptRatio();

        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.LOW_QUALITY_OPEN,
                "활동은 있었지만 실제 소비 제어 행동의 질이 낮아 아직 안정 상태로 보기 어렵다.",
                Map.ofEntries(
                        entry("streakDays", s.streakDays()),
                        entry("daysSinceLastEvent", s.daysSinceLastEvent()),
                        entry("recentEventCount3d", s.recentEventCount3d()),
                        entry("eventsCount", events),
                        entry("spendOpenCount", open),
                        entry("itemViewCount", view),
                        entry("purchaseAttemptCount", attempt),
                        entry("impulseSignalCount", impulse),
                        entry("purchaseCancelDoneCount", cancelDone),
                        entry("studiedToday", s.studiedToday()),
                        entry("passiveRatio", passiveRatio),
                        entry("attemptRatio", attemptRatio),
                        entry("passiveRatioMaxForSafe", PASSIVE_RATIO_MAX_FOR_SAFE),
                        entry("attemptRatioMinForSafe", ATTEMPT_RATIO_MIN_FOR_SAFE)
                )
        );
    }
}