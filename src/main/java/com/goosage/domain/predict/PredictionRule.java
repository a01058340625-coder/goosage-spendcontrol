package com.goosage.domain.predict;

import com.goosage.domain.spendcontrol.SpendControlSnapshot;

public interface PredictionRule {
    int priority();
    boolean matches(SpendControlSnapshot s);
    Prediction apply(SpendControlSnapshot s);
}