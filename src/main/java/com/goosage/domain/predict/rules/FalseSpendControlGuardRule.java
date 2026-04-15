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
    private static final double OPEN_RATIO_MAX_FOR_SAFE = 0.60;
    private static final double ACTION_RATIO_MIN_FOR_SAFE = 0.20;

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
        int action = s.state().quizSubmits();
        int risk = s.state().wrongReviews();
        int done = s.state().wrongReviewDoneCount();
        int justOpen = s.state().justOpenCount();

        if (events <= 0) {
            return false;
        }

        double openRatio = (double) justOpen / events;
        double actionRatio = (double) action / events;

        boolean recentEnough = s.recentEventCount3d() >= RECENT_3D_MIN;
        boolean stillRiskLeft = risk > 0;
        boolean tooManyOpenOnly = openRatio > OPEN_RATIO_MAX_FOR_SAFE;
        boolean tooLittleAction = actionRatio < ACTION_RATIO_MIN_FOR_SAFE;
        boolean noRecoveryDone = done == 0;

        // 1. 제어가 충분하고 현재 위험이 없으면 guard로 막지 않는다. (126 방어)
        if (risk == 0 && done >= 3) {
            return false;
        }

        // 2. action 1 / done 1 같은 약한 균형형은 과하게 LOW_QUALITY로 보내지 않는다. (123 방어)
        if (action <= 1 && done >= 1 && risk <= 1) {
            return false;
        }

        return recentEnough
                && (stillRiskLeft || tooManyOpenOnly || (tooLittleAction && noRecoveryDone));
    }

    @Override
    public Prediction apply(SpendControlSnapshot s) {
        int events = s.state().eventsCount();
        int action = s.state().quizSubmits();
        int risk = s.state().wrongReviews();
        int done = s.state().wrongReviewDoneCount();
        int justOpen = s.state().justOpenCount();

        double openRatio = events <= 0 ? 0.0 : (double) justOpen / events;
        double actionRatio = events <= 0 ? 0.0 : (double) action / events;

        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.LOW_QUALITY_OPEN,
                "활동은 있었지만 실제 소비 제어 행동의 질이 낮아 아직 안정 상태로 보기 어렵다.",
                Map.ofEntries(
                        entry("streakDays", s.streakDays()),
                        entry("daysSinceLastEvent", s.daysSinceLastEvent()),
                        entry("recentEventCount3d", s.recentEventCount3d()),
                        entry("eventsCount", events),
                        entry("quizSubmits", action),
                        entry("wrongReviews", risk),
                        entry("wrongReviewDoneCount", done),
                        entry("justOpenCount", justOpen),
                        entry("studiedToday", s.studiedToday()),
                        entry("openRatio", openRatio),
                        entry("actionRatio", actionRatio),
                        entry("openRatioMaxForSafe", OPEN_RATIO_MAX_FOR_SAFE),
                        entry("actionRatioMinForSafe", ACTION_RATIO_MIN_FOR_SAFE)
                )
        );
    }
}