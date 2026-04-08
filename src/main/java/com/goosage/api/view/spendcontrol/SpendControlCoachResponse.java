package com.goosage.api.view.spendcontrol;

public record SpendControlCoachResponse(
        SpendControlStateView state,
        NextActionView next,
        SpendControlPredictionView prediction
) {}
