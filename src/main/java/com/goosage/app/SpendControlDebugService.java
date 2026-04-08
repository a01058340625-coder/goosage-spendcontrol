package com.goosage.app;

import org.springframework.stereotype.Service;

import com.goosage.domain.spendcontrol.SpendControlDebugPort;

@Service
public class SpendControlDebugService {

    private final SpendControlDebugPort spendControlDebugPort;

    public SpendControlDebugService(SpendControlDebugPort spendControlDebugPort) {
        this.spendControlDebugPort = spendControlDebugPort;
    }

    public void recordPing(Long userId) {
        spendControlDebugPort.recordPing(userId);
    }
}