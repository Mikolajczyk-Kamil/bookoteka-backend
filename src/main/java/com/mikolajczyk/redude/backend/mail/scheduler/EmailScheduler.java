package com.mikolajczyk.redude.backend.mail.scheduler;

import com.mikolajczyk.redude.backend.domain.WeekMailObservers;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailScheduler {

    private final WeekMailObservers weekMailObservers;

    @Scheduled(cron = "0 0 10 * * MON")
    public void sendWeekMail() {
        weekMailObservers.notifyObservers();
    }
}
