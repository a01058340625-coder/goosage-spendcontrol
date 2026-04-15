package com.goosage.api.view.spendcontrol;

import com.goosage.domain.spendcontrol.SpendControlSnapshot;

public final class SpendControlMappers {

    private SpendControlMappers() {}

    public static SpendControlStateView toView(SpendControlSnapshot s) {
        if (s == null) {
            return new SpendControlStateView(
                    null,
                    false,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    null,
                    null
            );
        }

        return new SpendControlStateView(
                s.ymd(),
                s.studiedToday(),
                s.streakDays(),
                s.state().eventsCount(),
                s.state().spendOpenCount(),
                s.state().itemViewCount(),
                s.state().purchaseAttemptCount(),
                s.state().purchaseCancelDoneCount(),
                s.state().impulseSignalCount(),
                s.lastEventAt(),
                s.recentKnowledgeId()
        );
    }
}