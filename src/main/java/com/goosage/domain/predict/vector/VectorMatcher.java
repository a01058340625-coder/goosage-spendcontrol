package com.goosage.domain.predict.vector;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class VectorMatcher {

    private final Map<BehaviorPattern, ObservationVector> prototypes = new EnumMap<>(BehaviorPattern.class);

    public VectorMatcher() {
        prototypes.put(
                BehaviorPattern.LOW_ACTIVITY,
                new ObservationVector(0.10, 0.70, 0.10, 0.05, 0.05, 0.10, 0.10, 0.10, 0.80)
        );

        prototypes.put(
                BehaviorPattern.LOW_QUALITY_OPEN,
                new ObservationVector(0.55, 0.45, 0.25, 0.15, 0.05, 0.10, 0.50, 0.50, 0.20)
        );

        prototypes.put(
                BehaviorPattern.WRONG_HEAVY,
                new ObservationVector(0.75, 0.10, 0.10, 0.35, 0.05, 0.40, 0.60, 0.50, 0.10)
        );

        prototypes.put(
                BehaviorPattern.RECOVERY_PROGRESS,
                new ObservationVector(0.70, 0.10, 0.10, 0.25, 0.35, 0.10, 0.70, 0.60, 0.10)
        );

        prototypes.put(
                BehaviorPattern.HABIT_STABLE,
                new ObservationVector(0.80, 0.20, 0.20, 0.05, 0.35, 0.05, 0.80, 0.85, 0.05)
        );
    }

    public BehaviorPattern match(ObservationVector actual) {
        BehaviorPattern best = null;
        double bestDistance = Double.MAX_VALUE;

        for (Map.Entry<BehaviorPattern, ObservationVector> entry : prototypes.entrySet()) {
            double d = distance(actual, entry.getValue());
            if (d < bestDistance) {
                bestDistance = d;
                best = entry.getKey();
            }
        }

        return best;
    }

    public double distanceTo(ObservationVector actual, BehaviorPattern pattern) {
        ObservationVector prototype = prototypes.get(pattern);
        if (prototype == null) {
            return Double.MAX_VALUE;
        }
        return distance(actual, prototype);
    }

    private double distance(ObservationVector a, ObservationVector b) {
        return Math.abs(a.activity() - b.activity())
                + Math.abs(a.openRatio() - b.openRatio())
                + Math.abs(a.viewRatio() - b.viewRatio())
                + Math.abs(a.attemptRatio() - b.attemptRatio())
                + Math.abs(a.cancelDoneRatio() - b.cancelDoneRatio())
                + Math.abs(a.impulseRatio() - b.impulseRatio())
                + Math.abs(a.recentScore() - b.recentScore())
                + Math.abs(a.streakScore() - b.streakScore())
                + Math.abs(a.recencyPenalty() - b.recencyPenalty());
    }
}