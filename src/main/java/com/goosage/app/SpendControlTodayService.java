package com.goosage.app;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.goosage.domain.spendcontrol.SpendControlReadPort;
import com.goosage.domain.spendcontrol.TodayRow;

@Service
public class SpendControlTodayService {

    private final SpendControlReadPort spendControlReadPort;

    public SpendControlTodayService(SpendControlReadPort spendControlReadPort) {
        this.spendControlReadPort = spendControlReadPort;
    }

    public SpendControlTodayResult getToday(long userId) {

        LocalDate today = LocalDate.now();
        var rowOpt = spendControlReadPort.findToday(userId, today);

        int events = spendControlReadPort.todayEventCountFromEvents(userId, today);
        int actions = spendControlReadPort.todayActionFromEvents(userId, today);
        int risk = spendControlReadPort.todayRiskSignalFromEvents(userId, today);

        if (events == 0) {
            return new SpendControlTodayResult(0, 0, 0, "오늘 아직 기록이 없습니다");
        }

        TodayRow row = rowOpt.orElse(null);

        return new SpendControlTodayResult(
                events,
                actions,
                risk,
                messageFor(events, actions)
        );
    }

    private String messageFor(int events, int actions) {
        if (actions > 0) return "오늘 행동을 " + actions + "회 기록했어요";
        if (events > 0) return "오늘 활동 기록이 있습니다";
        return "오늘 아직 기록이 없습니다";
    }
}