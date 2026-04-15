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
public class HabitStableRule implements PredictionRule {

    private static final int STREAK_MIN = 3;
    private static final int RECENT_3D_MIN = 3;
    private static final int CONTROL_MIN = 2;
    private static final double PASSIVE_RATIO_MAX = 0.65;

    @Override
    public int priority() {
        return 25;
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

        if (s.streakDays() < STREAK_MIN) {
            return false;
        }

        if (s.recentEventCount3d() < RECENT_3D_MIN) {
            return false;
        }

        if (cancelDone < CONTROL_MIN) {
            return false;
        }

        if (attempt > 0 || impulse > 0) {
            return false;
        }

        double passiveRatio = s.openRatio() + s.viewRatio();
        return passiveRatio <= PASSIVE_RATIO_MAX;
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
        double controlRatio = s.cancelDoneRatio();

        return Prediction.of(
                PredictionLevel.SAFE,
                PredictionReasonCode.HABIT_STABLE,
                "소비 제어 루틴이 안정적으로 유지되고 있다.",
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
                        entry("controlRatio", controlRatio),
                        entry("streakMin", STREAK_MIN),
                        entry("recent3dMin", RECENT_3D_MIN),
                        entry("controlMin", CONTROL_MIN),
                        entry("passiveRatioMax", PASSIVE_RATIO_MAX)
                )
        );
    }
}