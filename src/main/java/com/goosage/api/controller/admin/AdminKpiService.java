package com.goosage.api.controller.admin;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AdminKpiService {

    private final JdbcTemplate jdbc;

    public AdminKpiService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Long> findActiveUserIdsToday() {
        String sql = """
            SELECT DISTINCT user_id
            FROM spendcontrol_events
            WHERE created_at >= CURDATE()
            ORDER BY user_id
        """;

        return jdbc.query(sql, (rs, rowNum) -> rs.getLong("user_id"));
    }
}