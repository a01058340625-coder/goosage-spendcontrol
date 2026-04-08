package com.goosage.api.view.spendcontrol;

import com.goosage.domain.EventType;

public record SpendControlEventRequest(
        EventType type,
        Long knowledgeId
) {
}