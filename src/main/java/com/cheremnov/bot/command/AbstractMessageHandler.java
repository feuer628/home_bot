package com.cheremnov.bot.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractMessageHandler implements IMessageHandler {

    @Autowired
    ApplicationContext context;

    public <T> T getBean(Class<T> requiredType) {
        return context.getBean(requiredType);
    }
}
