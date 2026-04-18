package com.goosage.domain.predict.rules;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.predict.PredictionLevel;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.predict.PredictionRule;
import com.goosage.domain.spendcontrol.SpendControlSnapshot;

@Component
public class WrongHeavyRule implements PredictionRule {

    private static final int RISK_MIN = 5;
    private static final int RISK_GAP_MIN = 3;
    private static final double RISK_RATIO_MIN = 0.35;

    @Override
    public int priority() {
        return 18;
    }

    @Override
    public boolean matches(SpendControlSnapshot s) {
        if (s == null || s.state() == null || !s.studiedToday()) {
            return false;
        }

        int attempt = s.state().purchaseAttemptCount();
        int impulse = s.state().impulseSignalCount();
        int cancelDone = s.state().purchaseCancelDoneCount();
        int controlAction = s.state().controlActionCount();
        int events = s.state().eventsCount();

        int risk = attempt + impulse;
        int defense = cancelDone + controlAction;
        int gap = risk - defense;
        double riskRatio = (double) risk / Math.max(events, 1);

        if (events < 5) {
            return false;
        }

        if (risk < RISK_MIN) {
            return false;
        }

        if (gap < RISK_GAP_MIN) {
            return false;
        }

        if (riskRatio < RISK_RATIO_MIN) {
            return false;
        }

        return true;
    }

    @Override
    public Prediction apply(SpendControlSnapshot s) {
        int attempt = s.state().purchaseAttemptCount();
        int impulse = s.state().impulseSignalCount();
        int cancelDone = s.state().purchaseCancelDoneCount();
        int controlAction = s.state().controlActionCount();
        int events = s.state().eventsCount();

        int risk = attempt + impulse;
        int defense = cancelDone + controlAction;
        int gap = risk - defense;
        double riskRatio = (double) risk / Math.max(events, 1);

        return Prediction.of(
                PredictionLevel.WARNING,
                PredictionReasonCode.WRONG_HEAVY,
                "위험 신호가 제어 흐름보다 강하다. 새 행동보다 제어 행동을 먼저 하자.",
                Map.of(
                        "impulseSignalCount", impulse,
                        "purchaseAttemptCount", attempt,
                        "purchaseCancelDoneCount", cancelDone,
                        "controlActionCount", controlAction,
                        "eventsCount", events,
                        "riskCount", risk,
                        "defenseCount", defense,
                        "riskGap", gap,
                        "riskRatio", riskRatio,
                        "riskRatioMin", RISK_RATIO_MIN
                )
        );
    }
}