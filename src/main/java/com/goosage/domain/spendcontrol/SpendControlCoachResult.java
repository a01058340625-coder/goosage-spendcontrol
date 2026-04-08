package com.goosage.domain.spendcontrol;

import com.goosage.domain.NextActionType;
import com.goosage.domain.predict.Prediction;

public record SpendControlCoachResult(
        SpendControlState state,
        Prediction prediction,
        NextActionType nextAction
) {}