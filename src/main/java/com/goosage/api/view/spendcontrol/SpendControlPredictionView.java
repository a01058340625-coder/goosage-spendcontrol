package com.goosage.api.view.spendcontrol;

// v1.4 계약(View) - 4필드 고정
public record SpendControlPredictionView(
        String level,
        String expectedOutcome,
        String reason,
        String minimalAction
) {}