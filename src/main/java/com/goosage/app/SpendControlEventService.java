package com.goosage.app;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goosage.domain.EventType;
import com.goosage.domain.spendcontrol.SpendControlEventPort;
import com.goosage.infra.log.EventLogWriter;

@Service
public class SpendControlEventService {

    private final SpendControlEventPort spendControlEventPort;

    public SpendControlEventService(SpendControlEventPort spendControlEventPort) {
        this.spendControlEventPort = spendControlEventPort;
    }

    @Transactional
    public void record(Long userId, EventType type, Long knowledgeId) {
        String refType = (knowledgeId == null) ? null : "KNOWLEDGE";

        spendControlEventPort.recordEvent(userId, type, refType, knowledgeId, null);

        EventLogWriter.write(userId, type.name());
    }
}