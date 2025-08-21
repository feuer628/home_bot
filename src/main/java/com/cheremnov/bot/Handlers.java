package com.cheremnov.bot;

import com.cheremnov.bot.command.AbstractCommandHandler;
import com.cheremnov.bot.command.ICallbackHandler;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Handlers {

    @Getter
    private final List<AbstractCommandHandler> commandHandlers;

    @Getter
    private final List<ICallbackHandler> callbackHandlers;

    // Использование конструктора для внедрения зависимостей
    @Autowired
    public Handlers(List<AbstractCommandHandler> commandHandlers, List<ICallbackHandler> callbackHandlers) {
        this.commandHandlers = commandHandlers;
        this.callbackHandlers = callbackHandlers;
    }
}
