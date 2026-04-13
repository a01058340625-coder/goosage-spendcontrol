package com.goosage.infra.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SpendControlStreakDao {

    private final JdbcTemplate jdbcTemplate;

    public SpendControlStreakDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int countStreak(long userId) {
        String sql = """
            SELECT COUNT(*)
            FROM spendcontrol_daily
            WHERE user_id = ?
              AND ymd <= CURDATE()
              AND (
                SELECT COUNT(*)
                FROM spendcontrol_daily d2
                WHERE d2.user_id = ?
                  AND d2.ymd BETWEEN spendcontrol_daily.ymd AND CURDATE()
              ) = DATEDIFF(CURDATE(), spendcontrol_daily.ymd) + 1
        """;

        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, userId, userId);
        return result != null ? result : 0;
    }
}