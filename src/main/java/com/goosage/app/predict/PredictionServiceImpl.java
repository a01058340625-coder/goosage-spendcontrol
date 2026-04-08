package com.goosage.app.predict;

import org.springframework.stereotype.Service;
import com.goosage.domain.predict.Prediction;
import com.goosage.domain.spendcontrol.SpendControlSnapshot;

@Service
public class PredictionServiceImpl implements PredictionService {

    private final PredictionEngine engine;

    public PredictionServiceImpl(PredictionEngine engine) {
        this.engine = engine;
    }

    @Override
    public Prediction predict(SpendControlSnapshot snapshot) {
        return engine.predict(snapshot); // ✅ 도메인 그대로 반환
    }
}