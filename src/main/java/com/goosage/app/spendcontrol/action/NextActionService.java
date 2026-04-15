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

        // 1. 오늘 완료는 즉시 종료
        if (reasonCode == PredictionReasonCode.TODAY_DONE) {
            return NextActionType.TODAY_SAFE;
        }

        // 2. 충동 / 고위험
        if (reasonCode == PredictionReasonCode.URGE_HIGH
                || reasonCode == PredictionReasonCode.RELAPSE_RISK
                || reasonCode == PredictionReasonCode.WRONG_HEAVY
                || reasonCode == PredictionReasonCode.REVIEW_WRONG_PENDING) {
            return NextActionType.PROCESS_IMPULSE_SIGNAL;
        }

        // 3. 데이터 부족 / 최소행동 / 공백
        if (reasonCode == PredictionReasonCode.DATA_POOR
                || reasonCode == PredictionReasonCode.MINIMUM_ACTION
                || reasonCode == PredictionReasonCode.LOW_ACTIVITY_3D
                || reasonCode == PredictionReasonCode.HABIT_COLLAPSE) {
            return NextActionType.MINIMUM_CONTACT;
        }

        // 4. 품질 낮은 open 위주
        if (reasonCode == PredictionReasonCode.LOW_QUALITY_OPEN) {
            return NextActionType.DO_CONTROL_ACTION;
        }

        // 5. 회복 진행 중
        if (reasonCode == PredictionReasonCode.RECOVERY_PROGRESS) {
            if (snap != null && snap.state() != null) {

                int impulse = snap.state().impulseSignalCount();
                int attempts = snap.state().purchaseAttempts();
                int blocked = snap.state().purchaseCancelDoneCount();
                int events = snap.state().eventsCount();

                // 충동만 두드러지면 즉시 신호 처리
                if (impulse > 0 && attempts <= 1 && blocked == 0) {
                    return NextActionType.PROCESS_IMPULSE_SIGNAL;
                }

                // 시도가 명확히 많고 제어가 부족할 때만 적극 개입
                if (attempts >= 3 && attempts > blocked) {
                    return NextActionType.DO_CONTROL_ACTION;
                }

                // 활동이 거의 없으면 최소 접촉
                if (events < 2) {
                    return NextActionType.MINIMUM_CONTACT;
                }

                // 그 외 회복 진행형은 점검
                return NextActionType.SPEND_CONTROL_CHECK;
            }

            return NextActionType.SPEND_CONTROL_CHECK;
        }

        // 6. 안정 구간
        if (reasonCode == PredictionReasonCode.RECOVERY_SAFE
                || reasonCode == PredictionReasonCode.HABIT_STABLE
                || reasonCode == PredictionReasonCode.GOOD_PROGRESS
                || reasonCode == PredictionReasonCode.STABLE_PROGRESS) {

            if (snap != null && snap.state() != null) {
                int impulse = snap.state().impulseSignalCount();
                int attempts = snap.state().purchaseAttempts();
                int blocked = snap.state().purchaseCancelDoneCount();
                int events = snap.state().eventsCount();

                // 안전하고 제어 행동이 충분하면 오늘 종료
                if (impulse == 0 && attempts <= 1 && blocked >= 4 && events >= 5) {
                    return NextActionType.TODAY_SAFE;
                }
            }

            return NextActionType.SPEND_CONTROL_CHECK;
        }

        return NextActionType.SPEND_CONTROL_CHECK;
    }
}