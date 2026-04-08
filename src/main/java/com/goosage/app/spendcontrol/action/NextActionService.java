package com.goosage.app.spendcontrol.action;

import org.springframework.stereotype.Service;

import com.goosage.domain.NextActionType;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.spendcontrol.SpendControlSnapshot;

@Service
public class NextActionService {

    public NextActionType decide(SpendControlSnapshot snap, PredictionReasonCode reasonCode) {

        if (reasonCode == null) {
            return NextActionType.SPEND_CONTROL_CHECK;
        }

        // 🔴 최우선: 충동 / 재발 위험
        if (reasonCode == PredictionReasonCode.URGE_HIGH
                || reasonCode == PredictionReasonCode.RELAPSE_RISK
                || reasonCode == PredictionReasonCode.WRONG_HEAVY
                || reasonCode == PredictionReasonCode.REVIEW_WRONG_PENDING) {

            return NextActionType.PROCESS_IMPULSE_SIGNAL;
        }

        // 🟡 행동 부족 / 흐름 깨짐
        if (reasonCode == PredictionReasonCode.DATA_POOR
                || reasonCode == PredictionReasonCode.MINIMUM_ACTION
                || reasonCode == PredictionReasonCode.LOW_ACTIVITY_3D
                || reasonCode == PredictionReasonCode.HABIT_COLLAPSE) {

            return NextActionType.MINIMUM_CONTACT;
        }

        // 🟠 품질 낮음 (열기만 함)
        if (reasonCode == PredictionReasonCode.LOW_QUALITY_OPEN) {
            return NextActionType.DO_CONTROL_ACTION;
        }

        // 🔵 회복 진행 중
        if (reasonCode == PredictionReasonCode.RECOVERY_PROGRESS) {

            if (snap != null && snap.state() != null) {

                int risk = snap.state().wrongReviews();
                int done = snap.state().wrongReviewDoneCount();
                int action = snap.state().quizSubmits();

                if (risk > 0 && done == 0) {
                    return NextActionType.PROCESS_IMPULSE_SIGNAL;
                }

                if (done > 0 && risk == 0) {
                    return NextActionType.DO_CONTROL_ACTION;
                }

                if (action < 2) {
                    return NextActionType.DO_CONTROL_ACTION;
                }
            }

            return NextActionType.SPEND_CONTROL_CHECK;
        }

        // 🟢 안정 구간
        if (reasonCode == PredictionReasonCode.RECOVERY_SAFE
                || reasonCode == PredictionReasonCode.HABIT_STABLE
                || reasonCode == PredictionReasonCode.GOOD_PROGRESS
                || reasonCode == PredictionReasonCode.STABLE_PROGRESS) {

            return NextActionType.SPEND_CONTROL_CHECK;
        }

        // 🟣 오늘 완료
        if (reasonCode == PredictionReasonCode.TODAY_DONE) {
            return NextActionType.TODAY_SAFE;
        }

        // fallback
        return NextActionType.SPEND_CONTROL_CHECK;
    }
}