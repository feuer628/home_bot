package com.cheremnov.bot.rest;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.db.subscibers.SubscriberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class BotController {

    @Autowired
    SubscriberRepository subscriberRepository;

    @Autowired
    private Bot bot;

    @GetMapping("/send-message")
    public void sendMessage() {
        subscriberRepository.findAll().forEach(subscriber -> {
            if (subscriber.getChatId() == null) {
                return;
            }
            bot.sendText(subscriber.getChatId(), "Ты есть - " + subscriber.getName());
        });
    }
}
