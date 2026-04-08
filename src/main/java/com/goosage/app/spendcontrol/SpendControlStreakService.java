package com.goosage.app.spendcontrol;

import org.springframework.stereotype.Service;

import com.goosage.domain.spendcontrol.SpendControlStreakPort;

@Service
public class SpendControlStreakService {

    private final SpendControlStreakPort recoveryStreakPort;

    public SpendControlStreakService(SpendControlStreakPort recoveryStreakPort) {
        this.recoveryStreakPort = recoveryStreakPort;
    }

    public int getStreak(long userId) {
        return recoveryStreakPort.countStreak(userId);
    }
}