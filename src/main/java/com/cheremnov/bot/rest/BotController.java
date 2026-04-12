package com.cheremnov.bot.rest;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.door.TuyaAdapter;
import com.cheremnov.bot.db.subscibers.SubscriberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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
