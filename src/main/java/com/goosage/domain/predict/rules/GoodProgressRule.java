package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.spendcontrol.SpendControlSnapshot;

@Component
public class GoodProgressRule implements PredictionRule {

    @Override
    public int priority() {
        return 60;
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
        int quiz = s.state().quizSubmits();
        int wrong = s.state().wrongReviews();
        int done = s.state().wrongReviewDoneCount();

        if (events < 5) {
            return false;
        }

        if (wrong != 0) {
            return false;
        }

        if (s.quizRatio() < 0.5) {
            return false;
        }

        // spendcontrol에서는 quizSubmits 안에 PURCHASE_ATTEMPT가 섞여 있으므로
        // 시도보다 제어가 부족한 상태는 GOOD_PROGRESS로 보내면 안 된다.
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
                "소비 제어 흐름이 안정적으로 진행 중이다. 지금 패턴을 유지하자.",
                Map.of(
                        "quizRatio", s.quizRatio(),
                        "eventsCount", s.state().eventsCount(),
                        "quizSubmits", s.state().quizSubmits(),
                        "wrongReviews", s.state().wrongReviews(),
                        "wrongReviewDoneCount", s.state().wrongReviewDoneCount()
                )
        );
    }
}