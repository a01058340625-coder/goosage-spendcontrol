package com.goosage.domain.spendcontrol;

import com.goosage.domain.EventType;

public interface SpendControlEventPort {
    void record(Long userId, EventType type, Long knowledgeId);
}