package com.goosage.infra.dao;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SpendControlReadJdbcDao implements SpendControlReadDao {

    private final JdbcTemplate jdbcTemplate;

    public SpendControlReadJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<SpendControlTodayRow> findToday(long userId) {
        String sql =
                "SELECT " +
                "  DATE(MAX(created_at)) AS ymd, " +
                "  COUNT(*) AS events_count, " +
                "  SUM(CASE WHEN type = 'SPEND_OPEN' THEN 1 ELSE 0 END) AS spend_open_count, " +
                "  SUM(CASE WHEN type = 'ITEM_VIEW' THEN 1 ELSE 0 END) AS item_view_count, " +
                "  SUM(CASE WHEN type = 'PURCHASE_ATTEMPT' THEN 1 ELSE 0 END) AS purchase_attempt_count, " +
                "  SUM(CASE WHEN type = 'PURCHASE_CANCEL_DONE' THEN 1 ELSE 0 END) AS purchase_cancel_done_count, " +
                "  SUM(CASE WHEN type = 'IMPULSE_SIGNAL' THEN 1 ELSE 0 END) AS impulse_signal_count, " +
                "  SUM(CASE WHEN type = 'CONTROL_ACTION' THEN 1 ELSE 0 END) AS control_action_count " +
                "FROM spendcontrol_events " +
                "WHERE user_id = ? " +
                "  AND DATE(created_at) = CURDATE() " +
                "HAVING COUNT(*) > 0";

        try {
            SpendControlTodayRow row = jdbcTemplate.queryForObject(
                    sql,
                    (rs, rowNum) -> new SpendControlTodayRow(
                            rs.getDate("ymd").toLocalDate(),
                            rs.getInt("events_count"),
                            rs.getInt("spend_open_count"),
                            rs.getInt("item_view_count"),
                            rs.getInt("purchase_attempt_count"),
                            rs.getInt("purchase_cancel_done_count"),
                            rs.getInt("impulse_signal_count"),
                            rs.getInt("control_action_count")
                    ),
                    userId
            );
            return Optional.ofNullable(row);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<LocalDateTime> lastEventAtAll(long userId) {
        String sql = "SELECT MAX(created_at) FROM spendcontrol_events WHERE user_id = ?";
        Timestamp ts = jdbcTemplate.queryForObject(sql, Timestamp.class, userId);
        return (ts == null) ? Optional.empty() : Optional.of(ts.toLocalDateTime());
    }

    @Override
    public int recentEventCount3d(long userId, LocalDate today) {
        String sql =
                "SELECT COUNT(*) " +
                "FROM spendcontrol_events " +
                "WHERE user_id = ? " +
                "  AND DATE(created_at) >= ? " +
                "  AND DATE(created_at) <= ?";

        LocalDate fromDate = today.minusDays(2);
        LocalDate toDate = today;

        Integer cnt = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                userId,
                java.sql.Date.valueOf(fromDate),
                java.sql.Date.valueOf(toDate)
        );

        return (cnt == null) ? 0 : cnt;
    }

    @Override
    public int calcStreakDays(long userId, LocalDate today) {
        String sql =
                "SELECT DISTINCT DATE(created_at) AS ymd " +
                "FROM spendcontrol_events " +
                "WHERE user_id = ? " +
                "ORDER BY ymd DESC";

        List<LocalDate> days = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getDate("ymd").toLocalDate(),
                userId
        );

        if (days.isEmpty()) {
            return 0;
        }

        LocalDate anchor = today;
        if (!days.contains(today)) {
            anchor = today.minusDays(1);
        }

        int streak = 0;
        LocalDate cursor = anchor;

        while (days.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }

        return streak;
    }

    @Override
    public int todaySpendOpenCountFromEvents(long userId, LocalDate today) {
        return countByType(userId, today, "SPEND_OPEN");
    }

    @Override
    public int todayItemViewCountFromEvents(long userId, LocalDate today) {
        return countByType(userId, today, "ITEM_VIEW");
    }

    @Override
    public int todayPurchaseAttemptCountFromEvents(long userId, LocalDate today) {
        return countByType(userId, today, "PURCHASE_ATTEMPT");
    }

    @Override
    public int todayPurchaseCancelDoneCountFromEvents(long userId, LocalDate today) {
        return countByType(userId, today, "PURCHASE_CANCEL_DONE");
    }

    @Override
    public int todayImpulseSignalCountFromEvents(long userId, LocalDate today) {
        return countByType(userId, today, "IMPULSE_SIGNAL");
    }

    @Override
    public int todayControlActionCountFromEvents(long userId, LocalDate today) {
        return countByType(userId, today, "CONTROL_ACTION");
    }

    @Override
    public int recentImpulseSignalCount3d(long userId, LocalDate today) {
        return countRecentByType(userId, today, "IMPULSE_SIGNAL");
    }

    @Override
    public int recentPurchaseCancelDoneCount3d(long userId, LocalDate today) {
        return countRecentByType(userId, today, "PURCHASE_CANCEL_DONE");
    }

    @Override
    public int recentControlActionCount3d(long userId, LocalDate today) {
        return countRecentByType(userId, today, "CONTROL_ACTION");
    }

    private int countByType(long userId, LocalDate today, String type) {
        String sql =
                "SELECT COUNT(*) FROM spendcontrol_events " +
                "WHERE user_id = ? AND type = ? AND created_at >= ? AND created_at < ?";

        Timestamp from = Timestamp.valueOf(today.atStartOfDay());
        Timestamp to = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, userId, type, from, to);
        return (cnt == null) ? 0 : cnt;
    }

    private int countRecentByType(long userId, LocalDate today, String type) {
        String sql =
                "SELECT COUNT(*) FROM spendcontrol_events " +
                "WHERE user_id = ? AND type = ? AND created_at >= ? AND created_at < ?";

        Timestamp from = Timestamp.valueOf(today.minusDays(2).atStartOfDay());
        Timestamp to = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        Integer cnt = jdbcTemplate.queryForObject(sql, Integer.class, userId, type, from, to);
        return (cnt == null) ? 0 : cnt;
    }
}