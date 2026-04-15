package com.goosage.domain.predict.vector;

public record ObservationVector(
        double activity,
        double openRatio,
        double viewRatio,
        double attemptRatio,
        double cancelDoneRatio,
        double impulseRatio,
        double recentScore,
        double streakScore,
        double recencyPenalty
) {
}