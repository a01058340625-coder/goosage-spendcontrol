package com.goosage.infra.dao;

import org.springframework.stereotype.Component;

import com.goosage.domain.EventType;
import com.goosage.domain.spendcontrol.SpendControlDebugPort;

@Component
public class SpendControlDebugDaoAdapter implements SpendControlDebugPort {

    private final SpendControlEventDao spendControlEventDao;

    public SpendControlDebugDaoAdapter(SpendControlEventDao spendControlEventDao) {
        this.spendControlEventDao = spendControlEventDao;
    }

    @Override
    public void recordPing(Long userId) {
        spendControlEventDao.recordEvent(userId, EventType.IMPULSE_SIGNAL, null, null, null, null);
    }
}