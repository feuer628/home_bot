package com.cheremnov.bot.command.start;

import com.cheremnov.bot.Bot;
import com.cheremnov.bot.command.AbstractCommandHandler;
import com.cheremnov.bot.db.subscibers.Subscriber;
import com.cheremnov.bot.db.subscibers.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.text.MessageFormat;

@Component
public class StartCommand extends AbstractCommandHandler {

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Override
    public boolean isCommandHidden() {
        return true;
    }

    @Override
    public boolean isPublicCommand() {
        return true;
    }

    @Override
    public String getCommandName() {
        return "start";
    }

    @Override
    public void handleCommand(Message message, Bot bot) {
        Subscriber subscriber = new Subscriber();
        User from = message.getFrom();
        subscriber.setId(from.getId());
        subscriber.setName(String.join(" ", from.getFirstName(), from.getLastName()));
        subscriber.setChatId(message.getChatId());
        subscriberRepository.save(subscriber);
        bot.sendText(message.getChatId(), MessageFormat.format("Привет, {0}!", subscriber.getName()));
    }
}
