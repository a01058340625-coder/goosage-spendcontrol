package com.goosage.api.view.spendcontrol;

import java.util.List;

public record PredictionView(
        String copy,
        ExposureLevel exposureLevel,
        List<EvidenceView> evidences,
        List<String> reasons
) {}
