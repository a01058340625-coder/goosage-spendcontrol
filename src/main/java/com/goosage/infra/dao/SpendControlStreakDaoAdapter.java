package com.goosage.infra.dao;

import org.springframework.stereotype.Repository;

import com.goosage.domain.spendcontrol.SpendControlStreakPort;

@Repository
public class SpendControlStreakDaoAdapter implements SpendControlStreakPort {

    private final SpendControlStreakDao studyStreakDao;

    public SpendControlStreakDaoAdapter(SpendControlStreakDao studyStreakDao) {
        this.studyStreakDao = studyStreakDao;
    }

    @Override
    public int countStreak(long userId) {
        return studyStreakDao.countStreak(userId);
    }
}
