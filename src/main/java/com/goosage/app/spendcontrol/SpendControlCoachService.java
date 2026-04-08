package com.goosage.app.spendcontrol;

import org.springframework.stereotype.Service;

import com.goosage.domain.spendcontrol.SpendControlCoachPort;
import com.goosage.domain.spendcontrol.SpendControlCoachResult;

@Service
public class SpendControlCoachService {

    private final SpendControlCoachPort recoveryCoachPort;

    public SpendControlCoachService(SpendControlCoachPort recoveryCoachPort) {
        this.recoveryCoachPort = recoveryCoachPort;
    }

    public SpendControlCoachResult coach(long userId) {
        return recoveryCoachPort.execute(userId);
    }
}