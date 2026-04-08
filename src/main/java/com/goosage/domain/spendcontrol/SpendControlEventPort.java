package com.goosage.domain.spendcontrol;

import com.goosage.domain.EventType;

public interface SpendControlEventPort {

    void recordEvent(long userId,
                     EventType type,
                     String targetType,
                     Long targetId,
                     String meta);
}