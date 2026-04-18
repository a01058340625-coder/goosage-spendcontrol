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

    private static final int EVENTS_MIN = 6;
    private static final int DEFENSE_MIN = 4;
    private static final int RECENT_3D_MIN = 3;
    private static final int DEFENSE_GAP_MIN = 3;
    private static final double PASSIVE_RATIO_MAX = 0.72;
    private static final double DEFENSE_RATIO_MIN = 0.35;
    private static final double STABILITY_RATIO_MIN = 0.45;

    @Override
    public int priority() {
        return 6;
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

        int risk = attempt + impulse;
        int defense = cancelDone + controlAction;
        int defenseGap = defense - risk;

        if (events < EVENTS_MIN) {
            return false;
        }

        if (defense < DEFENSE_MIN) {
            return false;
        }

        if (s.recentEventCount3d() < RECENT_3D_MIN) {
            return false;
        }

        if (risk <= 0) {
            return false;
        }

        if (defense <= risk) {
            return false;
        }

        if (defenseGap < DEFENSE_GAP_MIN) {
            return false;
        }

        double passiveRatio = s.openRatio() + s.viewRatio();
        double defenseRatio = (double) defense / Math.max(events, 1);
        double stabilityRatio = ((s.state().itemViewCount() * 0.5) + defense) / Math.max(events, 1);

        if (passiveRatio > PASSIVE_RATIO_MAX) {
            return false;
        }

        if (defenseRatio < DEFENSE_RATIO_MIN) {
            return false;
        }

        if (stabilityRatio < STABILITY_RATIO_MIN) {
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
        int controlAction = s.state().controlActionCount();

        int risk = attempt + impulse;
        int defense = cancelDone + controlAction;
        int defenseGap = defense - risk;

        double passiveRatio = s.openRatio() + s.viewRatio();
        double defenseRatio = (double) defense / Math.max(events, 1);
        double stabilityRatio = ((view * 0.5) + defense) / Math.max(events, 1);

        return Prediction.of(
                PredictionLevel.SAFE,
                PredictionReasonCode.RECOVERY_SAFE,
                "소비 흐름이 제어 행동 중심으로 안정화되었다. 현재 흐름을 유지하자.",
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
                        entry("controlActionCount", controlAction),
                        entry("riskCount", risk),
                        entry("defenseCount", defense),
                        entry("defenseGap", defenseGap),
                        entry("studiedToday", s.studiedToday()),
                        entry("eventsMin", EVENTS_MIN),
                        entry("defenseMin", DEFENSE_MIN),
                        entry("defenseGapMin", DEFENSE_GAP_MIN),
                        entry("defenseRatio", defenseRatio),
                        entry("defenseRatioMin", DEFENSE_RATIO_MIN),
                        entry("stabilityRatio", stabilityRatio),
                        entry("stabilityRatioMin", STABILITY_RATIO_MIN),
                        entry("passiveRatio", passiveRatio),
                        entry("passiveRatioMax", PASSIVE_RATIO_MAX)
                )
        );
    }
}