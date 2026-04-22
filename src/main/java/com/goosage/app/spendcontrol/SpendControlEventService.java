package com.goosage.app.spendcontrol;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goosage.domain.EventType;
import com.goosage.domain.spendcontrol.SpendControlEventPort;

@Service
public class SpendControlEventService {

    private final SpendControlEventPort spendControlEventPort;

    public SpendControlEventService(SpendControlEventPort spendControlEventPort) {
        this.spendControlEventPort = spendControlEventPort;
    }

    @Transactional
    public void record(Long userId, EventType type, Long knowledgeId, LocalDateTime occurredAt) {
        spendControlEventPort.record(userId, type, knowledgeId, occurredAt);
    }
}