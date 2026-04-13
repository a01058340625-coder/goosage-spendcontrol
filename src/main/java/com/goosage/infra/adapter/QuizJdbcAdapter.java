package com.goosage.infra.adapter;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.goosage.domain.EventType;

@Component
public class QuizJdbcAdapter {

    private final JdbcTemplate jdbc;

    public QuizJdbcAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void save(long userId, EventType type, Long knowledgeId) {
        String sql = """
            INSERT INTO spendcontrol_events (user_id, event_type, knowledge_id, created_at)
            VALUES (?, ?, ?, NOW())
        """;

        jdbc.update(
                sql,
                userId,
                type.name(),
                knowledgeId
        );
    }
}