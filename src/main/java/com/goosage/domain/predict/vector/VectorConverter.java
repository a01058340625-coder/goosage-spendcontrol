package com.goosage.domain.predict.vector;

import org.springframework.stereotype.Component;

import com.goosage.domain.spendcontrol.SpendControlSnapshot;

@Component
public class VectorConverter {

    public ObservationVector from(SpendControlSnapshot s) {

        double activity = clamp((s.state() != null ? s.state().eventsCount() : 0) / 8.0);

        double openRatio = s.openRatio();
        double viewRatio = s.viewRatio();
        double attemptRatio = s.attemptRatio();
        double cancelDoneRatio = s.cancelDoneRatio();
        double impulseRatio = s.impulseRatio();

        double recentScore = clamp(s.recentEventCount3d() / 12.0);
        double streakScore = clamp(s.streakDays() / 7.0);
        double recencyPenalty = clamp(s.daysSinceLastEvent() / 3.0);

        return new ObservationVector(
                activity,
                openRatio,
                viewRatio,
                attemptRatio,
                cancelDoneRatio,
                impulseRatio,
                recentScore,
                streakScore,
                recencyPenalty
        );
    }

    private double clamp(double value) {
        if (value < 0.0) return 0.0;
        if (value > 1.0) return 1.0;
        return value;
    }
}