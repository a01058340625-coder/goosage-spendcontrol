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
public class TodayDoneRule implements PredictionRule {

    private static final int CONTROL_DONE_MIN = 3;
    private static final double CONTROL_RATIO_MIN = 0.45;
    private static final double PASSIVE_RATIO_MAX = 0.50;

    @Override
    public int priority() {
        return 18;
    }

    @Override
    public boolean matches(SpendControlSnapshot s) {
        if (s == null || s.state() == null) {
            return false;
        }

        if (!s.studiedToday()) {
            return false;
        }

        int cancelDone = s.state().purchaseCancelDoneCount();
        int attempt = s.state().purchaseAttemptCount();
        int impulse = s.state().impulseSignalCount();

        double controlRatio = s.cancelDoneRatio();
        double passiveRatio = s.openRatio() + s.viewRatio();

        // 위험 신호 남아 있으면 today_done 불가
        if (attempt > 0 || impulse > 0) {
            return false;
        }

        return cancelDone >= CONTROL_DONE_MIN
                && controlRatio >= CONTROL_RATIO_MIN
                && passiveRatio <= PASSIVE_RATIO_MAX;
    }

    @Override
    public Prediction apply(SpendControlSnapshot s) {
        return Prediction.of(
                PredictionLevel.SAFE,
                PredictionReasonCode.TODAY_DONE,
                "오늘 소비 제어 행동이 충분히 이루어져 안전 종료 상태에 들어왔다.",
                Map.ofEntries(
                        entry("streakDays", s.streakDays()),
                        entry("daysSinceLastEvent", s.daysSinceLastEvent()),
                        entry("recentEventCount3d", s.recentEventCount3d()),
                        entry("eventsCount", s.state().eventsCount()),
                        entry("spendOpenCount", s.state().spendOpenCount()),
                        entry("itemViewCount", s.state().itemViewCount()),
                        entry("purchaseAttemptCount", s.state().purchaseAttemptCount()),
                        entry("purchaseCancelDoneCount", s.state().purchaseCancelDoneCount()),
                        entry("impulseSignalCount", s.state().impulseSignalCount()),
                        entry("studiedToday", s.studiedToday()),
                        entry("controlDoneMin", CONTROL_DONE_MIN),
                        entry("controlRatio", s.cancelDoneRatio()),
                        entry("passiveRatio", s.openRatio() + s.viewRatio()),
                        entry("controlRatioMin", CONTROL_RATIO_MIN),
                        entry("passiveRatioMax", PASSIVE_RATIO_MAX)
                )
        );
    }
}