package com.cheremnov.bot.schedule;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.db.subscibers.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SubscribersSchedule {

    @Autowired
    private Bot bot;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Scheduled(cron = "@weekly")
    public void performRegularTask() {
        try {
            bot.sendAllTrustedUsers("Количество подписантов: " + subscriberRepository.count());
        } catch (Exception e) {
            bot.sendAllTrustedUsers("Ошибка при получении списка подписчиков: " + e.getMessage());
            throw e;
        }
    }
}
