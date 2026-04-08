package com.goosage.api.view.spendcontrol;

import org.springframework.stereotype.Component;

import com.goosage.app.predict.PredictionCopyService;

@Component
public class PredictionViewMapper {

    private final PredictionCopyService copyService;

    public PredictionViewMapper(PredictionCopyService copyService) {
        this.copyService = copyService;
    }

    // CoachPredictionView -> SpendControlPredictionView
    public SpendControlPredictionView toView(CoachPredictionView p) {
        var copy = copyService.render(p);

        ExposureLevel exposureLevel = ExposurePolicy.decide(p);

        String reason =
                exposureLevel == ExposureLevel.DETAIL
                        ? p.reasonCode().description()
                        : "";

        return new SpendControlPredictionView(
                p.level().name(),
                copy.expectedOutcome(),
                reason,
                copy.minimalAction()
        );
    }

    public SpendControlPredictionView toDto(CoachPredictionView p) {
        return toView(p);
    }
}