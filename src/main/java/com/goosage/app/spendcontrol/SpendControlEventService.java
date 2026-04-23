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
    public void record(
            Long userId,
            EventType type,
            Long knowledgeId,
            LocalDateTime occurredAt,
            boolean isBrainAction,
            String brainActionType,
            String brainPatternType
    ) {
        String payloadJson = buildPayloadJson(type, isBrainAction, brainActionType, brainPatternType);
        String source = isBrainAction ? "brain" : "user";

        spendControlEventPort.record(userId, type, knowledgeId, occurredAt, payloadJson, source);
    }

    private String buildPayloadJson(
            EventType type,
            boolean isBrainAction,
            String brainActionType,
            String brainPatternType
    ) {
        if (!isBrainAction) {
            return "{\"source\":\"user\"}";
        }

        String actionValue = (brainActionType == null || brainActionType.isBlank())
                ? type.name()
                : brainActionType;

        String patternValue = (brainPatternType == null || brainPatternType.isBlank())
                ? ""
                : ",\"brainPatternType\":\"" + brainPatternType + "\"";

        return "{\"source\":\"brain\",\"brainActionType\":\"" + actionValue + "\"" + patternValue + "}";
    }
}