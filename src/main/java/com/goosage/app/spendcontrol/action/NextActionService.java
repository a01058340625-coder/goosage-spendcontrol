package com.goosage.app.spendcontrol.action;

import org.springframework.stereotype.Service;

import com.goosage.domain.NextActionType;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.spendcontrol.SpendControlSnapshot;

@Service
public class NextActionService {

    public NextActionType decide(SpendControlSnapshot snap, PredictionReasonCode reasonCode) {

        if (reasonCode == null) {
            return NextActionType.MINIMUM_CONTACT;
        }

        if (reasonCode == PredictionReasonCode.TODAY_DONE
                || reasonCode == PredictionReasonCode.HABIT_STABLE) {
            return NextActionType.TODAY_SAFE;
        }

        if (reasonCode == PredictionReasonCode.GOOD_PROGRESS
                || reasonCode == PredictionReasonCode.RECOVERY_SAFE) {
            return NextActionType.SPEND_CONTROL_CHECK;
        }

        if (reasonCode == PredictionReasonCode.RECOVERY_SAFE) {
            return NextActionType.SPEND_CONTROL_CHECK;
        }

        if (reasonCode == PredictionReasonCode.REVIEW_WRONG_PENDING
                || reasonCode == PredictionReasonCode.URGE_HIGH
                || reasonCode == PredictionReasonCode.WRONG_HEAVY) {
            return NextActionType.PROCESS_IMPULSE_SIGNAL;
        }

        if (reasonCode == PredictionReasonCode.RELAPSE_RISK
                || reasonCode == PredictionReasonCode.LOW_QUALITY_OPEN) {
            return NextActionType.DO_CONTROL_ACTION;
        }

        if (reasonCode == PredictionReasonCode.RECOVERY_PROGRESS
                || reasonCode == PredictionReasonCode.STABLE_PROGRESS) {

            if (snap != null && snap.state() != null) {
                int impulse = snap.state().impulseSignalCount();
                int attempts = snap.state().purchaseAttemptCount();
                int cancelDone = snap.state().purchaseCancelDoneCount();

                if (impulse > 0 && attempts <= 1 && cancelDone == 0) {
                    return NextActionType.PROCESS_IMPULSE_SIGNAL;
                }

                if (attempts >= 3 && cancelDone < attempts) {
                    return NextActionType.DO_CONTROL_ACTION;
                }
            }

            return NextActionType.SPEND_CONTROL_CHECK;
        }

        return NextActionType.MINIMUM_CONTACT;
    }
}