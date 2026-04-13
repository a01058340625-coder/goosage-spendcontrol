package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.spendcontrol.SpendControlSnapshot;

@Component
public class ControlGoodProgressRule implements PredictionRule {

    private static final int DONE_MIN = 2;
    private static final int RECENT_3D_MIN = 3;

    @Override
    public int priority() {
        return 22;
    }

    @Override
    public boolean matches(SpendControlSnapshot s) {
        if (s == null || s.state() == null) return false;
        if (!s.studiedToday()) return false;

        int wrong = s.state().wrongReviews();
        int done = s.state().wrongReviewDoneCount();
        int quiz = s.state().quizSubmits();

        // 기본 회복 흐름 조건
        if (done < DONE_MIN) {
            return false;
        }

        if (done < wrong) {
            return false;
        }

        if (s.recentEventCount3d() < RECENT_3D_MIN) {
            return false;
        }

        // spendcontrol에서는 quizSubmits 안에 PURCHASE_ATTEMPT가 섞여 있으므로
        // 시도(quiz)가 제어(done)보다 많으면 GOOD_PROGRESS로 보내면 안 된다.
        if (quiz >= 3 && done < quiz) {
            return false;
        }

        return true;
    }

    @Override
    public Prediction apply(SpendControlSnapshot s) {
        return Prediction.of(
                PredictionLevel.SAFE,
                PredictionReasonCode.GOOD_PROGRESS,
                "충동을 제어하는 흐름이 잘 유지되고 있다. 지금 리듬을 계속 이어가자.",
                Map.of(
                        "riskSignal", s.state().wrongReviews(),
                        "recoveryAction", s.state().wrongReviewDoneCount(),
                        "quizSubmits", s.state().quizSubmits(),
                        "recentEventCount3d", s.recentEventCount3d(),
                        "streakDays", s.streakDays(),
                        "daysSinceLastEvent", s.daysSinceLastEvent()
                )
        );
    }
}