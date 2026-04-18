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
public class SpendControlProgressRule implements PredictionRule {

    private static final int EVENTS_MIN = 4;
    private static final int RECENT_3D_MIN = 2;

    private static final double PASSIVE_RATIO_MAX = 0.72;
    private static final double DEFENSE_RATIO_MIN = 0.25;

    @Override
    public int priority() {
        return 8;
    }

    @Override
    public boolean matches(SpendControlSnapshot s) {
        if (s == null || s.state() == null || !s.studiedToday()) {
            return false;
        }

        int impulse = s.state().impulseSignalCount();
        int attempts = s.state().purchaseAttemptCount();
        int cancelDone = s.state().purchaseCancelDoneCount();
        int controlAction = s.state().controlActionCount();
        int events = s.state().eventsCount();

        int risk = attempts + impulse;
        int defense = cancelDone + controlAction;
        int defenseGap = defense - risk;

        if (events < EVENTS_MIN) {
            return false;
        }

        if (s.recentEventCount3d() < RECENT_3D_MIN) {
            return false;
        }

        if (risk <= 0) {
            return false;
        }

        if (defense <= 0) {
            return false;
        }

        if (defense < risk) {
            return false;
        }

        // progress는 "위험과 방어가 비슷하거나 막 역전한 구간"만 허용
        if (defenseGap > 1) {
            return false;
        }

        double passiveRatio = s.openRatio() + s.viewRatio();
        double defenseRatio = (double) defense / Math.max(events, 1);

        // browse-heavy / passive-heavy는 progress로 과대평가하지 않음
        if (passiveRatio > PASSIVE_RATIO_MAX) {
            return false;
        }

        // 방어가 너무 약하면 progress로 보지 않음
        if (defenseRatio < DEFENSE_RATIO_MIN) {
            return false;
        }

        return true;
    }

    @Override
    public Prediction apply(SpendControlSnapshot s) {
        int events = s.state() != null ? s.state().eventsCount() : 0;
        int open = s.state() != null ? s.state().spendOpenCount() : 0;
        int view = s.state() != null ? s.state().itemViewCount() : 0;
        int impulse = s.state() != null ? s.state().impulseSignalCount() : 0;
        int attempts = s.state() != null ? s.state().purchaseAttemptCount() : 0;
        int cancelDone = s.state() != null ? s.state().purchaseCancelDoneCount() : 0;
        int controlAction = s.state() != null ? s.state().controlActionCount() : 0;

        int risk = attempts + impulse;
        int defense = cancelDone + controlAction;
        int defenseGap = defense - risk;

        double passiveRatio = s.openRatio() + s.viewRatio();
        double defenseRatio = (double) defense / Math.max(events, 1);

        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.RECOVERY_PROGRESS,
                "위험 흐름이 남아 있지만 제어 행동이 붙고 있다. 제어 흐름을 유지하며 안정 구간으로 넘어가자.",
                Map.ofEntries(
                        entry("streakDays", s.streakDays()),
                        entry("daysSinceLastEvent", s.daysSinceLastEvent()),
                        entry("recentEventCount3d", s.recentEventCount3d()),
                        entry("eventsCount", events),
                        entry("spendOpenCount", open),
                        entry("itemViewCount", view),
                        entry("impulseSignalCount", impulse),
                        entry("purchaseAttemptCount", attempts),
                        entry("purchaseCancelDoneCount", cancelDone),
                        entry("controlActionCount", controlAction),
                        entry("riskCount", risk),
                        entry("defenseCount", defense),
                        entry("defenseGap", defenseGap),
                        entry("eventsMin", EVENTS_MIN),
                        entry("recentEventCount3dMin", RECENT_3D_MIN),
                        entry("passiveRatio", passiveRatio),
                        entry("passiveRatioMax", PASSIVE_RATIO_MAX),
                        entry("defenseRatio", defenseRatio),
                        entry("defenseRatioMin", DEFENSE_RATIO_MIN)
                )
        );
    }
}