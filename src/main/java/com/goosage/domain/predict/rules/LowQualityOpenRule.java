package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.spendcontrol.SpendControlSnapshot;

@Component
public class LowQualityOpenRule implements PredictionRule {

    private static final int EVENTS_MIN = 5;
    private static final double OPEN_RATIO_MIN = 0.55;
    private static final double QUIZ_RATIO_MAX = 0.45;

    @Override
    public int priority() {
        return 20;
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
        double openRatio = s.openRatio();
        double quizRatio = s.quizRatio();

        int attempts = s.state().quizSubmits();
        int blocked = s.state().wrongReviewDoneCount();

        System.out.println(
                "[LOW_QUALITY_OPEN] events=" + events
                + ", attempts=" + attempts
                + ", blocked=" + blocked
                + ", openRatio=" + openRatio
                + ", quizRatio=" + quizRatio
        );

        if (blocked >= 3) {
            return false;
        }

        if (attempts <= 1) {
            return false;
        }

        return events >= EVENTS_MIN
                && openRatio >= OPEN_RATIO_MIN
                && quizRatio <= QUIZ_RATIO_MAX;
    }

    @Override
    public Prediction apply(SpendControlSnapshot s) {
        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.LOW_QUALITY_OPEN,
                "열기 비중이 높고 제어가 부족해. 실제 소비 제어 행동을 1개 추가하자.",
                Map.of(
                        "openRatio", s.openRatio(),
                        "quizRatio", s.quizRatio(),
                        "eventsCount", s.state().eventsCount(),
                        "studiedToday", s.studiedToday()
                )
        );
    }
}