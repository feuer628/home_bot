package com.cheremnov.bot.command;

import com.cheremnov.bot.messenger.Messenger;
import com.cheremnov.bot.messenger.MsgMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractMessageHandler {

    @Autowired
    ApplicationContext context;

    public <T> T getBean(Class<T> requiredType) {
        return context.getBean(requiredType);
    }

    public final void handle(MsgMessage message, Messenger messenger) {
        if (handleMessage(message, messenger)) {
            messenger.deleteMessageHandler(message.getChatId());
        }
    }

    public abstract boolean handleMessage(MsgMessage message, Messenger messenger);
}