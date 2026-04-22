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
public class ControlActionOnlyRule implements PredictionRule {

    private static final int EVENTS_MIN = 5;
    private static final int CONTROL_ACTION_MIN = 3;
    private static final int RECENT_3D_MIN = 3;
    private static final double PASSIVE_RATIO_MAX = 0.75;
    private static final double CONTROL_RATIO_MIN = 0.35;

    @Override
    public int priority() {
        return 13;
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
        int controlAction = s.state().controlActionCount();

        if (events < EVENTS_MIN) {
            return false;
        }

        if (s.recentEventCount3d() < RECENT_3D_MIN) {
            return false;
        }

        if (controlAction < CONTROL_ACTION_MIN) {
            return false;
        }

        if (attempt > 0 || impulse > 0) {
            return false;
        }

        if (cancelDone > 0) {
            return false;
        }

        double passiveRatio = s.openRatio() + s.viewRatio();
        if (passiveRatio > PASSIVE_RATIO_MAX) {
            return false;
        }

        if (s.controlActionRatio() < CONTROL_RATIO_MIN) {
            return false;
        }

        return true;
    }

    @Override
    public Prediction apply(SpendControlSnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.RECOVERY_PROGRESS,
                "제어 행동이 반복적으로 유지되고 있다. 현재 제어 흐름을 이어가자.",
                Map.ofEntries(
                        entry("eventsCount", s.state().eventsCount()),
                        entry("spendOpenCount", s.state().spendOpenCount()),
                        entry("itemViewCount", s.state().itemViewCount()),
                        entry("purchaseAttemptCount", s.state().purchaseAttemptCount()),
                        entry("purchaseCancelDoneCount", s.state().purchaseCancelDoneCount()),
                        entry("impulseSignalCount", s.state().impulseSignalCount()),
                        entry("controlActionCount", s.state().controlActionCount()),
                        entry("controlActionRatio", s.controlActionRatio()),
                        entry("recentEventCount3d", s.recentEventCount3d()),
                        entry("streakDays", s.streakDays()),
                        entry("daysSinceLastEvent", s.daysSinceLastEvent())
                )
        );
    }
}