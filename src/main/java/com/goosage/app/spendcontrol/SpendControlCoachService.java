package com.goosage.app.spendcontrol;

import org.springframework.stereotype.Service;

import com.goosage.domain.spendcontrol.SpendControlCoachPort;
import com.goosage.domain.spendcontrol.SpendControlCoachResult;

@Service
public class SpendControlCoachService {

    private final SpendControlCoachPort spendControlCoachPort;

    public SpendControlCoachService(SpendControlCoachPort spendControlCoachPort) {
        this.spendControlCoachPort = spendControlCoachPort;
    }

    public SpendControlCoachResult coach(long userId) {
        return spendControlCoachPort.execute(userId);
    }
}