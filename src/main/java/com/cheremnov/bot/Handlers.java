package com.cheremnov.bot;

import com.cheremnov.bot.command.ICallbackHandler;
import com.cheremnov.bot.command.ICommandHandler;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Component
public class Handlers {

    @Getter
    private final List<ICommandHandler> commandHandlers;

    @Getter
    private final List<ICallbackHandler> callbackHandlers;

    // Использование конструктора для внедрения зависимостей
    @Autowired
    public Handlers(List<ICommandHandler> commandHandlers, List<ICallbackHandler> callbackHandlers) {
        this.commandHandlers = commandHandlers;
        this.callbackHandlers = callbackHandlers;
    }
}
