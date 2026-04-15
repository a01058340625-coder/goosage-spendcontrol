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
public class SpendControlSafeRule implements PredictionRule {

    private static final int EVENTS_MIN = 5;
    private static final int CANCEL_DONE_MIN = 2;
    private static final int RECENT_3D_MIN = 3;
    private static final double PASSIVE_RATIO_MAX = 0.70;
    private static final double CANCEL_RATIO_MIN = 0.20;

    @Override
    public int priority() {
        return 16;
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

        if (s.recentEventCount3d() < RECENT_3D_MIN) {
            return false;
        }

        if (impulse > 0) {
            return false;
        }

        // attempt가 남아 있는데 cancel이 덮지 못하면 safe 아님
        if (attempt > 0 && cancelDone < attempt) {
            return false;
        }

        double passiveRatio = s.openRatio() + s.viewRatio();
        double cancelRatio = s.cancelDoneRatio();

        if (passiveRatio > PASSIVE_RATIO_MAX) {
            return false;
        }

        if (cancelRatio < CANCEL_RATIO_MIN) {
            return false;
        }

        return true;
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
        double cancelRatio = s.cancelDoneRatio();

        return Prediction.of(
                PredictionLevel.SAFE,
                PredictionReasonCode.RECOVERY_SAFE,
                "소비 시도와 충동 신호가 제어 완료 흐름으로 안정화되었다.",
                Map.ofEntries(
                        entry("streakDays", s.streakDays()),
                        entry("daysSinceLastEvent", s.daysSinceLastEvent()),
                        entry("recentEventCount3d", s.recentEventCount3d()),
                        entry("eventsCount", events),
                        entry("spendOpenCount", open),
                        entry("itemViewCount", view),
                        entry("purchaseAttemptCount", attempt),
                        entry("purchaseCancelDoneCount", cancelDone),
                        entry("impulseSignalCount", impulse),
                        entry("studiedToday", s.studiedToday()),
                        entry("eventsMin", EVENTS_MIN),
                        entry("cancelDoneMin", CANCEL_DONE_MIN),
                        entry("cancelRatio", cancelRatio),
                        entry("cancelRatioMin", CANCEL_RATIO_MIN),
                        entry("passiveRatio", passiveRatio),
                        entry("passiveRatioMax", PASSIVE_RATIO_MAX)
                )
        );
    }
}