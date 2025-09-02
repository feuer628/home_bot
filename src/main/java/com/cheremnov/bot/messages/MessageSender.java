package com.cheremnov.bot.messages;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.db.subscibers.Subscriber;
import com.cheremnov.bot.db.subscibers.SubscriberRepository;
import com.cheremnov.bot.db.trusted_user.TrustedUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageSender {

    @Autowired
    private Bot bot;

    @Autowired
    private TrustedUserRepository trustedUserRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    public void sendAllTrustedUsers(String message) {
        trustedUserRepository.findAll().forEach(trustedUser -> {
            subscriberRepository.findById(trustedUser.getId()).ifPresent(
                    subscriber -> sendToSubscriber(subscriber, message));
        });
    }

    public void sendSubscribers(String message, List<Long> subscriberIds) {
        if (subscriberIds != null) {
            subscriberRepository.findAllById(subscriberIds).forEach(subscriber -> sendToSubscriber(subscriber, message));
        }
    }

    public void sendAllSubscribers(String message) {
        subscriberRepository.findAll().forEach(subscriber -> sendToSubscriber(subscriber, message));
    }

    private void sendToSubscriber(Subscriber subscriber, String message) {
        if (subscriber.getChatId() != null) {
            bot.sendText(subscriber.getChatId(), message);
        }
    }
}
