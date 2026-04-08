package com.goosage.infra.adapter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.goosage.app.predict.PredictionService;
import com.goosage.app.spendcontrol.action.NextActionService;
import com.goosage.domain.NextActionType;
import com.goosage.domain.predict.Prediction;
import com.goosage.domain.spendcontrol.SpendControlCoachPort;
import com.goosage.domain.spendcontrol.SpendControlCoachResult;
import com.goosage.domain.spendcontrol.SpendControlSnapshot;
import com.goosage.domain.spendcontrol.SpendControlSnapshotService;
import com.goosage.domain.spendcontrol.SpendControlState;

@Component
public class SpendControlCoachAdapter implements SpendControlCoachPort {

    private final SpendControlSnapshotService spendControlSnapshotService;
    private final NextActionService nextActionService;
    private final PredictionService predictionService;

    public SpendControlCoachAdapter(
            SpendControlSnapshotService spendControlSnapshotService,
            NextActionService nextActionService,
            PredictionService predictionService
    ) {
        this.spendControlSnapshotService = spendControlSnapshotService;
        this.nextActionService = nextActionService;
        this.predictionService = predictionService;
    }

    @Override
    public SpendControlCoachResult execute(long userId) {

        SpendControlSnapshot snap = spendControlSnapshotService.snapshot(
                userId,
                LocalDate.now(),
                LocalDateTime.now()
        );
        SpendControlState state = snap.state();

        Prediction prediction = predictionService.predict(snap);
        NextActionType next = nextActionService.decide(snap, prediction.reasonCode());

        return new SpendControlCoachResult(state, prediction, next);
    }
}