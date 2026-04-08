package com.goosage.api.view.spendcontrol;

import com.goosage.domain.spendcontrol.SpendControlSnapshot;

public final class PredictionMappers {

    private PredictionMappers() {}

    public static PredictionInput toPredictionInput(long userId, SpendControlSnapshot s) {
        if (s == null) {
            return PredictionInput.of(userId, 0, 9999, 0);
        }
        return PredictionInput.of(
                userId,
                s.streakDays(),
                s.daysSinceLastEvent(),
                s.recentEventCount3d()
        );
    }
}
