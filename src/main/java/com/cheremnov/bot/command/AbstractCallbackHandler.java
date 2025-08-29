package com.cheremnov.bot.command;

import com.cheremnov.bot.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public abstract class AbstractCallbackHandler implements ICallbackHandler {

    @Autowired
    private ApplicationContext context;

    public <T> T getBean(Class<T> requiredType) {
        return context.getBean(requiredType);
    }

    @Override
    public abstract String callbackPrefix();

    @Override
    public final void handle(CallbackQuery callback, Bot bot) {
        handleCallback(callback, bot);
        bot.answerCallback(callback);
    }

    public abstract void handleCallback(CallbackQuery callback, Bot bot);
}
