package com.goosage.infra.dao;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.goosage.domain.EventType;
import com.goosage.domain.spendcontrol.SpendControlEventPort;

@Component
public class SpendControlEventDaoAdapter implements SpendControlEventPort {

    private final SpendControlEventDao spendControlEventDao;

    public SpendControlEventDaoAdapter(SpendControlEventDao spendControlEventDao) {
        this.spendControlEventDao = spendControlEventDao;
    }

    @Override
    public void record(Long userId, EventType type, Long knowledgeId, LocalDateTime occurredAt) {
        String refType = (knowledgeId == null) ? null : "KNOWLEDGE";
        spendControlEventDao.recordEvent(userId, type, refType, knowledgeId, null, occurredAt);
    }
}