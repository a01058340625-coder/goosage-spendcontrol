package com.goosage.infra.dao;

import org.springframework.stereotype.Component;
import com.goosage.domain.EventType;
import com.goosage.domain.spendcontrol.SpendControlEventPort;

@Component
public class SpendControlEventDaoAdapter implements SpendControlEventPort {

    private final SpendControlEventDao studyEventDao;

    public SpendControlEventDaoAdapter(SpendControlEventDao studyEventDao) {
        this.studyEventDao = studyEventDao;
    }

    @Override
    public void recordEvent(long userId, EventType eventType, String refType, Long refId, String payloadJson) {
        studyEventDao.recordEvent(userId, eventType, refType, refId, payloadJson);
    }
}