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
    private static final int ACTION_MIN = 2;
    private static final double OPEN_RATIO_MAX = 0.50;

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
        int action = s.state().quizSubmits();
        int risk = s.state().wrongReviews();
        int justOpen = s.state().justOpenCount();

        if (events <= 0) {
            return false;
        }

        if (s.streakDays() < STREAK_MIN) {
            return false;
        }

        if (s.recentEventCount3d() < RECENT_3D_MIN) {
            return false;
        }

        if (action < ACTION_MIN) {
            return false;
        }

        if (risk > 0) {
            return false;
        }

        double openRatio = (double) justOpen / events;
        return openRatio <= OPEN_RATIO_MAX;
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
                PredictionLevel.SAFE,
                PredictionReasonCode.HABIT_STABLE,
                "소비 제어 루틴이 안정적으로 유지되고 있다.",
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
                        entry("streakMin", STREAK_MIN),
                        entry("recent3dMin", RECENT_3D_MIN),
                        entry("actionMin", ACTION_MIN),
                        entry("openRatioMax", OPEN_RATIO_MAX)
                )
        );
    }
}