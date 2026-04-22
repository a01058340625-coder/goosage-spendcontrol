package com.goosage.infra.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.goosage.domain.EventType;

@Repository
public class SpendControlEventDao {

    private static final Logger log =
            LoggerFactory.getLogger(SpendControlEventDao.class);

    private final JdbcTemplate jdbcTemplate;

    public SpendControlEventDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void recordEvent(long userId,
                            EventType eventType,
                            String refType,
                            Long refId,
                            String payloadJson,
                            LocalDateTime occurredAt) {

        LocalDateTime eventTime = (occurredAt == null) ? LocalDateTime.now() : occurredAt;
        Long sessionId = findActiveSessionId(userId);

        if (sessionId == null) {
            sessionId = createSession(userId, eventTime);
        }

        insertEvent(sessionId, userId, eventType, refType, refId, payloadJson, eventTime);
        touchSession(sessionId, eventTime);
        upsertDaily(userId, eventTime.toLocalDate(), eventType, eventTime);
    }

    private Long findActiveSessionId(long userId) {
        String sql = """
            SELECT id
            FROM spendcontrol_sessions
            WHERE user_id = ?
              AND ended_at IS NULL
            ORDER BY started_at DESC
            LIMIT 1
        """;

        var list = jdbcTemplate.query(sql, (rs, i) -> rs.getLong("id"), userId);
        return list.isEmpty() ? null : list.get(0);
    }

    private long createSession(long userId, LocalDateTime eventTime) {
        String sql = """
            INSERT INTO spendcontrol_sessions (user_id, started_at, total_events, last_event_at)
            VALUES (?, ?, 0, ?)
        """;

        jdbcTemplate.update(
                sql,
                userId,
                java.sql.Timestamp.valueOf(eventTime),
                java.sql.Timestamp.valueOf(eventTime)
        );

        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        return (id == null) ? 0L : id;
    }

    private void insertEvent(long sessionId,
                             long userId,
                             EventType eventType,
                             String refType,
                             Long refId,
                             String payloadJson,
                             LocalDateTime eventTime) {

        String sql = """
            INSERT INTO spendcontrol_events (
                session_id,
                user_id,
                type,
                event_type,
                ref_type,
                ref_id,
                payload_json,
                created_at
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(
                sql,
                sessionId,
                userId,
                eventType.name(),
                eventType.name(),
                refType,
                refId,
                payloadJson,
                java.sql.Timestamp.valueOf(eventTime)
        );

        log.info(
                "[SPEND_EVENT][INSERT] sessionId={}, userId={}, eventType={}, eventTime={}",
                sessionId,
                userId,
                eventType.name(),
                eventTime
        );
    }

    private void touchSession(long sessionId, LocalDateTime eventTime) {
        String sql = """
            UPDATE spendcontrol_sessions
            SET total_events = total_events + 1,
                last_event_at = ?
            WHERE id = ?
        """;

        jdbcTemplate.update(
                sql,
                java.sql.Timestamp.valueOf(eventTime),
                sessionId
        );
    }

    private void upsertDaily(long userId, LocalDate ymd, EventType eventType, LocalDateTime eventTime) {
        int quizInc = (eventType == EventType.ITEM_VIEW || eventType == EventType.PURCHASE_ATTEMPT) ? 1 : 0;
        int wrongInc = (eventType == EventType.IMPULSE_SIGNAL || eventType == EventType.PURCHASE_ATTEMPT) ? 1 : 0;
        int wrongDoneInc = (eventType == EventType.PURCHASE_CANCEL_DONE) ? 1 : 0;

        String sql = """
            INSERT INTO spendcontrol_daily (
                user_id,
                ymd,
                events_count,
                quiz_submits,
                wrong_reviews,
                wrong_review_done_count,
                last_event_at
            )
            VALUES (?, ?, 1, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                events_count = events_count + 1,
                quiz_submits = quiz_submits + VALUES(quiz_submits),
                wrong_reviews = wrong_reviews + VALUES(wrong_reviews),
                wrong_review_done_count = wrong_review_done_count + VALUES(wrong_review_done_count),
                last_event_at = VALUES(last_event_at)
        """;

        jdbcTemplate.update(
                sql,
                userId,
                java.sql.Date.valueOf(ymd),
                quizInc,
                wrongInc,
                wrongDoneInc,
                java.sql.Timestamp.valueOf(eventTime)
        );
    }
}