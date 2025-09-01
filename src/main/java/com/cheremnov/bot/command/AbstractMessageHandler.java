package com.cheremnov.bot.command;

import com.cheremnov.bot.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public abstract class AbstractMessageHandler {

    @Autowired
    ApplicationContext context;

    public <T> T getBean(Class<T> requiredType) {
        return context.getBean(requiredType);
    }

    public final void handle(Message message, Bot bot) {
        if (handleMessage(message, bot)){
            bot.deleteMessageHandler(message.getChatId());
        }
    }

    public abstract boolean handleMessage(Message message, Bot bot);
}
