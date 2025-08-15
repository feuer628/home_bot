package com.cheremnov.bot.command;

import com.cheremnov.bot.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractCommandHandler implements ICommandHandler {

    @Autowired
    ApplicationContext context;

    @Autowired
    UserRepository userRepository;

    <T> T getBean(Class<T> requiredType) {
        return context.getBean(requiredType);
    }
}
