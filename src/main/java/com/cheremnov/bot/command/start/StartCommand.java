package com.cheremnov.bot.command.start;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgMessage;
import com.cheremnov.bot.messenger.MsgUser;
import com.cheremnov.bot.command.help.HelpCommand;
import com.cheremnov.bot.db.subscibers.Subscriber;
import com.cheremnov.bot.db.subscibers.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component
public class StartCommand extends HelpCommand {

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Override
    public String getCommandName() {
        return "start";
    }

    @Override
    public void handleCommand(MsgMessage message, Messenger messenger) {
        Subscriber subscriber = new Subscriber();
        MsgUser from = message.getFrom();
        subscriber.setId(from.getId());
        subscriber.setName(from.getFullName());
        subscriber.setChatId(message.getChatId());
        subscriberRepository.save(subscriber);
        messenger.sendText(message.getChatId(), MessageFormat.format("Привет, {0}!\n\n", subscriber.getName()) + HELP_TEXT);
    }
}