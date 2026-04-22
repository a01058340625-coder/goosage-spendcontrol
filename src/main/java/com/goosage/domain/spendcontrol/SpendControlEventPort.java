package com.goosage.domain.spendcontrol;

import java.time.LocalDateTime;

import com.goosage.domain.EventType;

public interface SpendControlEventPort {
    void record(Long userId, EventType type, Long knowledgeId, LocalDateTime occurredAt);
}