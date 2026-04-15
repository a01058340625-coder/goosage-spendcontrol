package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.spendcontrol.SpendControlSnapshot;

@Component
public class SpendControlProgressRule implements PredictionRule {

    @Override
    public int priority() {
        return 12;
    }

    @Override
    public boolean matches(SpendControlSnapshot s) {
        if (s == null || s.state() == null || !s.studiedToday()) {
            return false;
        }

        int impulse = s.state().wrongReviews();
        int attempts = s.state().quizSubmits();
        int blocked = s.state().wrongReviewDoneCount();
        int events = s.state().eventsCount();

        // 제어 행동이 없으면 progress 아님
        if (blocked <= 0) {
            return false;
        }

        // 이미 안전권으로 볼 수 있는 경우 progress로 잡지 않음
        if (impulse == 0 && attempts <= 1 && blocked >= 4 && events >= 5) {
            return false;
        }

        // 약한 균형형(123 같은 케이스)은 progress 과매칭 방지
        if (attempts <= 1 && blocked <= 1) {
            return false;
        }

        // 실제 소비 시도 흐름이 있어야 progress로 본다
        return attempts >= 2;
    }

    @Override
    public Prediction apply(SpendControlSnapshot s) {
        int events = s.state() != null ? s.state().eventsCount() : 0;
        int impulse = s.state() != null ? s.state().wrongReviews() : 0;
        int attempts = s.state() != null ? s.state().quizSubmits() : 0;
        int blocked = s.state() != null ? s.state().wrongReviewDoneCount() : 0;

        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.RECOVERY_PROGRESS,
                "소비 시도 흐름 속에서도 제어 행동이 붙고 있어. 제어 흐름을 이어가자.",
                Map.of(
                        "streakDays", s.streakDays(),
                        "daysSinceLastEvent", s.daysSinceLastEvent(),
                        "recentEventCount3d", s.recentEventCount3d(),
                        "eventsCount", events,
                        "wrongReviews", impulse,
                        "quizSubmits", attempts,
                        "wrongReviewDoneCount", blocked
                )
        );
    }
}