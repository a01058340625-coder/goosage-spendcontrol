package com.goosage.app.spendcontrol;

import org.springframework.stereotype.Service;

import com.goosage.domain.spendcontrol.SpendControlStreakPort;

@Service
public class SpendControlStreakService {

    private final SpendControlStreakPort spendControlStreakPort;

    public SpendControlStreakService(SpendControlStreakPort spendControlStreakPort) {
        this.spendControlStreakPort = spendControlStreakPort;
    }

    public int getStreak(long userId) {
        return spendControlStreakPort.countStreak(userId);
    }
}