package com.goosage.app.predict;

import com.goosage.domain.predict.Prediction;
import com.goosage.domain.spendcontrol.SpendControlSnapshot;

public interface PredictionService {
    Prediction predict(SpendControlSnapshot snapshot); // ✅ SSOT 계약
}